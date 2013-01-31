/**
 * Created with IntelliJ IDEA.
 * User: Sebastien
 * Date: 31/01/13
 * Time: 10:56
 * To change this template use File | Settings | File Templates.
 */

import com.taztag.tazpad.kevoree.Telegram;
import org.kevoree.annotation.*;
import org.kevoree.framework.AbstractComponentType;
import org.kevoree.framework.MessagePort;

import java.lang.String;
import java.util.StringTokenizer;


/**
 * Défniniton des ports
 */
@Library(name = "JavaSE")
@Requires({
        @RequiredPort(name = "setLight", type = PortType.MESSAGE, optional = true)
})
@Provides({
        @ProvidedPort(name = "trameReceiver", type = PortType.MESSAGE)
})
/*@DictionaryType({
        @DictionaryAttribute(name = "Board_light", defaultValue = "3/0/1", optional = true)

})   */

@ComponentType
public class enOceanDispatcher extends AbstractComponentType {


    @Start
    public void startComponent() {
        System.out.println("EnOceanDispatcher :: Start");
    }
    @Stop
    public void stopComponent() {
        System.out.println("EnOceanDispatcher :: Stop");
    }

    @Update
    public void updateComponent() {
        System.out.println("EnOceanDispatcher :: Update");
        stopComponent();
        startComponent();
    }


    @Port(name = "trameReceiver")
    public void lectureTrame(Object str) {

        if(str instanceof String){
            String trame = str.toString();
            Telegram telgram = new Telegram(trame);
        }
        else{
            System.out.println("Incorrect Trame Format");
        }
    }

}
