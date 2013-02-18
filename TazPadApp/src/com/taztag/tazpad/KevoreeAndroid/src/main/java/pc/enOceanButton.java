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
        @RequiredPort(name = "BBas", type = PortType.MESSAGE, optional = true),
        @RequiredPort(name = "BHaut", type = PortType.MESSAGE, optional = true),
        @RequiredPort(name = "BPresse", type = PortType.MESSAGE, optional = true),
        @RequiredPort(name = "BRelache", type = PortType.MESSAGE, optional = true)
})
@Provides({
        @ProvidedPort(name = "trameReceiver", type = PortType.MESSAGE)
})
/*@DictionaryType({
        @DictionaryAttribute(name = "Board_light", defaultValue = "3/0/1", optional = true)

})   */

@ComponentType

public class enOceanButton extends AbstractComponentType{

    String trame;
    Telegram telegram ;
    MessagePort bh ;
    MessagePort bb ;
    MessagePort bp ;
    MessagePort br ;


    @Start
    public void startComponent() {
        System.out.println("EnOceanButton :: Start");
    }
    @Stop
    public void stopComponent() {
        System.out.println("EnOceanButton :: Stop");
    }

    @Update
    public void updateComponent() {
        System.out.println("EnOceanButton :: Update");
        stopComponent();
        startComponent();
    }

    @Port(name = "trameReceiver")
    public void lectureTrame(Object str) {

        if(str instanceof String){
            System.out.println("Analyse Button Trame");
            trame = str.toString();
            telegram = new Telegram(trame);

            if (telegram.getData().equals("Bouton_Bas")) {
                bb = getPortByName("BBas", MessagePort.class);
                bb.process("EnOcean:Button:ButtonBas:True");

            }
            else if (telegram.getData().equals("Bouton_Haut")){
                bh = getPortByName("BHaut", MessagePort.class);
                bh.process("EnOcean:Button:ButtonHaut:True");

            }
            else if(telegram.getData().equals("Bouton_Presse")){
                bp = getPortByName("BPresse", MessagePort.class);
                bp.process("EnOcean:Button:ButtonPresse:True");
            }
            else if(telegram.getData().equals("Bouton_Relache")){
                br = getPortByName("BRelache", MessagePort.class);
                br.process("EnOcean:Button:ButtonRelache:True");
            }

        }

        else {System.out.println("Incorrect Trame");}



    }








}
