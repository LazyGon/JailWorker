package fr.alienationgaming.jailworker.events;

public class JailDeleteEvent extends JailWorkerCancellableEvent {

    private String jailName;

    public JailDeleteEvent(String jailName) {
        cancel = false;
        this.jailName = jailName;
    }

    /**
     * @return the jailName
     */
    public String getJailName() {
        return jailName;
    }
}