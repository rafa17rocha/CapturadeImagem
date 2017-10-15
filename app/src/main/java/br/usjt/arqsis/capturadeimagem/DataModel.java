package br.usjt.arqsis.capturadeimagem;

public class DataModel
{

	public String name;
	boolean checked;
	public boolean enabled;

	DataModel(String name, boolean checked, boolean enabled)
	{
		this.name = name;
		this.checked = checked;
		this.enabled = enabled;
	}

}