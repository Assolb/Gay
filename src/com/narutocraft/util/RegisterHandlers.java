package com.narutocraft.util;

import com.narutocraft.banks.NationalBank;
import com.narutocraft.duels.DuelsHandler;
import com.narutocraft.events.EventsHandler;
import com.narutocraft.exams.ExamsHandler;
import com.narutocraft.hospital.HospitalHandler;
import com.narutocraft.report.ReportHandler;
import com.narutocraft.society.Society;
import com.narutocraft.stats.ConfigPlayer;
import com.narutocraft.teams.Team;
import com.narutocraft.village.Village;

public class RegisterHandlers {

	public static void register()
	{
		EventsHandler.enablePlugin();
		
		ExamsHandler.enablePlugin();
	    
	    ConfigPlayer.enablePlugin();
	    
	    Society.enablePlugin();
	    
	    Team.enablePlugin();
	    
	    Village.enablePlugin();
	    
	    Lang.enablePlugin();
	    
	    ReportHandler.enablePlugin();
	    
	    DuelsHandler.enablePlugin();
	    
	    NationalBank.enablePlugin();
	    
	    HospitalHandler.enablePlugin();
	}
}
