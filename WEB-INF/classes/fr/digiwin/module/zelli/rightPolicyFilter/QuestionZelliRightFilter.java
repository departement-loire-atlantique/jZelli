package fr.digiwin.module.zelli.rightPolicyFilter;

import com.jalios.jcms.Channel;
import com.jalios.jcms.Group;
import com.jalios.jcms.Member;
import com.jalios.jcms.Publication;
import com.jalios.jcms.policy.BasicRightPolicyFilter;
import com.jalios.util.Util;

import generated.QuestionZelli;

public class QuestionZelliRightFilter extends BasicRightPolicyFilter {

    @Override
    public boolean canBeReadBy(boolean isAuthorized, Publication pub, Member mbr, boolean searchInGroups) {
        if (!isAuthorized || Util.isEmpty(mbr) || mbr.isAdmin()) {
            return isAuthorized;
        }

        if (!(pub instanceof QuestionZelli) || mbr.isAdmin(pub.getWorkspace())) {
            return isAuthorized;
        }
        
        if(pub.getAuthor().equals(mbr)) {
            return isAuthorized;
        }
        
        Group gestQuestRepGrp = Channel.getChannel().getGroup("$jcmsplugin.zelli.groupe.gestionnaires.id");
        if(searchInGroups && Util.notEmpty(gestQuestRepGrp) && mbr.belongsToGroup(gestQuestRepGrp)) {
            return isAuthorized;
        }

        return false;
    }

}
