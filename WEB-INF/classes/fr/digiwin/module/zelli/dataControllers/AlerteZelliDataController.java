package fr.digiwin.module.zelli.dataControllers;

import java.util.Map;

import com.jalios.jcms.BasicDataController;
import com.jalios.jcms.Data;
import com.jalios.jcms.Member;
import com.jalios.util.Util;

import generated.AlerteZelli;

public class AlerteZelliDataController extends BasicDataController {

	@Override
	public void beforeWrite(Data data, int op, Member mbr, Map ctx) {
		if (op != OP_UPDATE || !(data instanceof AlerteZelli)) {
			return;
		}

		AlerteZelli alerte = (AlerteZelli) data;
		AlerteZelli alerteOld = (AlerteZelli) ctx.get(CTXT_PREVIOUS_DATA);

		if (Util.notEmpty(alerteOld) && alerte.getPstatus() == 10 && alerteOld.getPstatus() != 10) {
			//TODO send alert Firebase
		}
	}

}
