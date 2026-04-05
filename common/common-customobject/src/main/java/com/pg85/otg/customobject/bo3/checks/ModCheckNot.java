package com.pg85.otg.customobject.bo3.checks;

public class ModCheckNot extends ModCheck
{
	@Override
	public String makeString()
	{
		return makeString("ModCheckNot");
	}
	
	public boolean evaluate()
	{
		return !super.evaluate();
	}
}
