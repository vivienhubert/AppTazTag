#include <jni.h>
#include <string.h>
#include <android/log.h>
#define DEBUG_TAG "NDK_TazPad"

#include <stdio.h>
#include <errno.h>
#include <pthread.h>
#include <fcntl.h>
#include <sys/epoll.h>
#include <math.h>
#include <time.h>
#include <sys/ioctl.h>
#include <sys/time.h>

#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <signal.h>

#define  LOG_TAG  "enocean module"
#include <termios.h>


#define  EO_DEBUG 1

#if EO_DEBUG
#  define  D(...)   printf(__VA_ARGS__)
#else
#  define  D(...)   ((void)0)
#endif

#define EO_DEVICE	"/dev/ttyUSB0"
#define DEV_PWR_PM	"/dev/device-pm"
#define DEVICE_PM_IOM                  	'P'
#define EO_PWR_OFF 0
#define EO_PWR_ON 1
#define EO_BAUDRATE	57600

int eo_fd;
pthread_mutex_t eo_lock = PTHREAD_MUTEX_INITIALIZER;


static speed_t getBaudrate(int baudrate);
static int hw_eo_port_config(int fd, int baudrate);
static void eo_init();
static void eo_finish();
static void *eo_read_thread(void *ptr);


static int mode;


int Java_com_taztag_tazpad_app_AndroidNDK1SampleActivity_main(JNIEnv * env, jobject this)
{

  printf("********************\n");
  printf("* ENOCEAN Listener *\n");
  printf("********************\n\n");

  eo_init();

  pthread_t thread1;
  char *message1 = "Thread 1";
  int  iret1;

  iret1 = pthread_create( &thread1, NULL, eo_read_thread, (void*) message1);

  sleep(10000);

  eo_finish();

  return 0;
}


static speed_t getBaudrate(int baudrate)
{
	switch(baudrate) {
	case 0: return B0;
	case 50: return B50;
	case 75: return B75;
	case 110: return B110;
	case 134: return B134;
	case 150: return B150;
	case 200: return B200;
	case 300: return B300;
	case 600: return B600;
	case 1200: return B1200;
	case 1800: return B1800;
	case 2400: return B2400;
	case 4800: return B4800;
	case 9600: return B9600;
	case 19200: return B19200;
	case 38400: return B38400;
	case 57600: return B57600;
	case 115200: return B115200;
	case 230400: return B230400;
	case 460800: return B460800;
	case 500000: return B500000;
	case 576000: return B576000;
	case 921600: return B921600;
	case 1000000: return B1000000;
	case 1152000: return B1152000;
	case 1500000: return B1500000;
	case 2000000: return B2000000;
	case 2500000: return B2500000;
	case 3000000: return B3000000;
	case 3500000: return B3500000;
	case 4000000: return B4000000;
	default: return -1;
	}
}


static int hw_eo_port_config(int fd, int baudrate)
{
	int    err;
	speed_t speed;
	struct termios cfg;

	/* Check arguments */
	speed = getBaudrate(baudrate);
	if (speed == -1) {
		printf("Invalid baudrate\n");
		return -1;
	}

	/* Configure device */
	printf("Configuring serial port baudrate to %d \n",baudrate);
	if (tcgetattr(fd, &cfg)){
		printf("tcgetattr() failed\n");
		close(fd);
		return -1;
	}

	cfmakeraw(&cfg);
	cfsetispeed(&cfg,B57600);
	cfsetospeed(&cfg,B57600);

	cfg.c_cflag &= ~PARENB;    // set no parity, stop bits, data bits
	cfg.c_cflag &= ~CSTOPB;
	cfg.c_cflag &= ~CSIZE;
	cfg.c_cflag |= CS8;


	if (tcsetattr(fd, TCSANOW, &cfg)){
		printf("tcsetattr() failed\n");
		close(fd);
		return -1;
	}
	return 0;
}



static void eo_init(  )
{

	printf("EO INIT\n");

	eo_fd = open(EO_DEVICE, O_RDWR);

    if (eo_fd < 0) {
        D("no eo emulation detected\n");
        return;
    }

	fcntl(eo_fd, F_SETFL, 0);

	if(hw_eo_port_config(eo_fd, EO_BAUDRATE) != 0){
		printf("hw_eo_port_config fail\n");
		return;
	}

    D("eo  will read from '%s' \n", EO_DEVICE);

    D("eo initialized\n");
}


static void *eo_read_thread(void *ptr)
{
	char buff[256];
	int i, ret;

	int ma_socket;
	struct sockaddr_in mon_address, client_address;
	int mon_address_longueur, lg;

	bzero(&mon_address,sizeof(mon_address));
	mon_address.sin_port = htons(30000);
	mon_address.sin_family = AF_INET;
	mon_address.sin_addr.s_addr = htonl(INADDR_ANY);

	/* creation de socket */
	if ((ma_socket = socket(AF_INET,SOCK_STREAM,0))== -1)
	{
  		printf("la creation rate\n");
  		exit(0);
	}
	//signal(SIGINT,fin);
	/* bind serveur - socket */
	bind(ma_socket,(struct sockaddr *)&mon_address,sizeof(mon_address));

	/* ecoute sur la socket */
	listen(ma_socket,5);

	/* accept la connexion */
	mon_address_longueur = sizeof(client_address);

	printf("\nDATA RECEIVED ->\n");

	for (;;) {

	      pthread_mutex_lock(&eo_lock);
		  ret = read( eo_fd, buff, sizeof(buff) );
		  pthread_mutex_unlock(&eo_lock);

		  if (ret < 0) {
		    if (errno == EINTR)
		      continue;
		    if (errno == EWOULDBLOCK)
		      continue;
		    printf("error while reading from eo daemon socket: %s:\n", strerror(errno));
		    D("eo closed\n");
		    break;
		  }else{
		  for(i=0;i<ret;i++){
		    if(buff[i]==0x55)
		      printf("\nFrame received ->  ");
		    printf("%02x ", buff[i]);
		  }
		  while(1){
  			ma_socket = accept(ma_socket,
                         (struct sockaddr *)&client_address,
                         &mon_address_longueur);

  			if (fork() == 0){
    			close(ma_socket);
				lg = read(ma_socket,buff, 512);
    			printf("le serveur a recu: %s\n",buff);
    			sprintf(buff,"%s du serveur",buff);
    			write(ma_socket,buff, 512);
    			shutdown(ma_socket,2);
    			close(ma_socket);
    			exit(0);
  			}
		}
		shutdown(ma_socket,2);
		close(ma_socket);
		}
	}
}


static void eo_finish()
{
    D("TAZTAG finished\n");

    close( eo_fd );
}


