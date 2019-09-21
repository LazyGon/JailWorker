package fr.alienationgaming.jailworker;

public class GetConfigValues {

	JailWorker plugin;
	public GetConfigValues(JailWorker jailworker){
		plugin = jailworker;
	}
	public int Blocks(){
		return (plugin.getConfig().getInt("Jails.Blocks"));
	}
	public int MaxSand(){
		return (plugin.getConfig().getInt("Jails.MaxSand"));
	}
	public long Speed(){
		return (plugin.getConfig().getLong("Jails.Speed"));
	}
	public boolean prisonersSpeak()
	{
		return (plugin.getConfig().getBoolean("Prisoners.Speak"));
	}
	public boolean prisonersEar()
	{
		return (plugin.getConfig().getBoolean("Prisoners.Ear"));
	}
	public boolean adminCmds()
	{
		return (plugin.getConfig().getBoolean("Prisoners.Admin-cmds"));
	}
}
