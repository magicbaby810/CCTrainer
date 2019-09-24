package com.sk.cctrainer;

import com.billy.cc.core.component.CC;
import com.billy.cc.core.component.CCResult;
import com.billy.cc.core.component.CCUtil;
import com.billy.cc.core.component.IComponent;
import com.sk.cctrainer.event.RefreshEvent;
import com.sk.demo_common_base.utils.RxBus;

public class ComponentApp implements IComponent {
    @Override
    public String getName() {
        return "ComponentApp";

    }

    @Override
    public boolean onCall(CC cc) {
        String actionName = cc.getActionName();
        switch (actionName) {
            case "showMainActivity":
                openMainActivity(cc);
                break;
            case "refreshText":
                refreshText(cc);
                break;
            default:
                break;
        }

        return false;
    }

    private void openMainActivity(CC cc) {
        CCUtil.navigateTo(cc, MainActivity.class);
        CC.sendCCResult(cc.getCallId(), CCResult.success());
    }

    private void refreshText(CC cc) {
        RxBus.getDefault().post(new RefreshEvent(cc.getParamItem("text")));
        CC.sendCCResult(cc.getCallId(), CCResult.success());
    }

}
