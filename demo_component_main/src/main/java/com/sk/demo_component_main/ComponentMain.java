package com.sk.demo_component_main;

import com.billy.cc.core.component.CC;
import com.billy.cc.core.component.CCResult;
import com.billy.cc.core.component.CCUtil;
import com.billy.cc.core.component.IComponent;

public class ComponentMain implements IComponent {
    @Override
    public String getName() {
        return "ComponentMain";

    }

    @Override
    public boolean onCall(CC cc) {
        String actionName = cc.getActionName();
        switch (actionName) {
            case "showTrainerActivity":
                openTrainerActivity(cc);
                break;
            default:
                break;
        }

        return false;
    }

    private void openTrainerActivity(CC cc) {
        CCUtil.navigateTo(cc, TrainerActivity.class);
        CC.sendCCResult(cc.getCallId(), CCResult.success());
    }
}
