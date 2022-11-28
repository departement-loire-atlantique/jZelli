package fr.digiwin.module.zelli.utils;

import com.jalios.jcms.Member;
import com.jalios.jcms.accesscontrol.AccessControlManager;
import com.jalios.util.Util;

public class ZelliManager {

	public static final String ACL_CAN_USE_TDB = "admin/reporting/plugins-zelli-can-use-tdb";
	private AccessControlManager aclMgr = AccessControlManager.getInstance();

	private static final ZelliManager SINGLETON = new ZelliManager();

	private ZelliManager() {
	}

	public static ZelliManager getInstance() {
		return SINGLETON;
	}

	public boolean canUseTdb(Member paramMember) {
		if (Util.notEmpty(paramMember)) {
			AccessControlManager accessControlManager = AccessControlManager.getInstance();
			return (paramMember.isAdmin() || accessControlManager.checkAccess(paramMember, ACL_CAN_USE_TDB, null));
		}
		return false;
	}

}