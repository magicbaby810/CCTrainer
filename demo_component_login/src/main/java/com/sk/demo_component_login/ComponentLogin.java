package com.sk.demo_component_login;

import com.billy.cc.core.component.CC;
import com.billy.cc.core.component.CCResult;
import com.billy.cc.core.component.CCUtil;
import com.billy.cc.core.component.IComponent;

public class ComponentLogin implements IComponent {
    @Override
    public String getName() {
        return "ComponentLogin";

    }

    @Override
    public boolean onCall(CC cc) {
        String actionName = cc.getActionName();
        switch (actionName) {
            case "showLoginActivity":
                openLoginActivity(cc);
                break;
            default:
                break;
        }

        return false;
    }

    private void openLoginActivity(CC cc) {
        CCUtil.navigateTo(cc, LoginActivity.class);
        CC.sendCCResult(cc.getCallId(), CCResult.success());
    }
}
