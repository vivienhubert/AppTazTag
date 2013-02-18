package pc;

import org.kevoree.annotation.*;
import org.kevoree.framework.AbstractComponentType;
import org.kevoree.framework.MessagePort;

/**
 * Created with IntelliJ IDEA.
 * User: Sebastien
 * Date: 18/02/13
 * Time: 11:10
 * To change this template use File | Settings | File Templates.
 */

@Library(name = "Android")

@Requires({
        @RequiredPort(name = "Temp", type = PortType.MESSAGE, optional = true)

})
@Provides({
        @ProvidedPort(name = "trameReceiver", type = PortType.MESSAGE)
})
/*@DictionaryType({
        @DictionaryAttribute(name = "Board_light", defaultValue = "3/0/1", optional = true)

})   */

@ComponentType


public class enOceanTemp extends AbstractComponentType {

    String trame;
    Telegram telegram;
    MessagePort temp;

    @Start
    public void startComponent() {
        System.out.println("EnOceanTemp :: Start");
    }
    @Stop
    public void stopComponent() {
        System.out.println("EnOceanTemp :: Stop");
    }

    @Update
    public void updateComponent() {
        System.out.println("EnOceanTemp :: Update");
        stopComponent();
        startComponent();
    }


    @Port(name = "trameReceiver")
    public void lectureTrame(Object str){

        trame = str.toString();
        telegram = new Telegram(trame);


        if(str instanceof String){
                System.out.println("Analyse Temp Trame");
                temp = getPortByName("Temp", MessagePort.class);
                temp.process("EnOcean:Temp:TempValue:"+telegram.getData());
                System.out.println("EnOcean:Temp:TempValue:"+telegram.getData());
        }

}

}


