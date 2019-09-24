package com.sk.demo_component_message;

import com.billy.cc.core.component.CC;
import com.billy.cc.core.component.CCResult;
import com.billy.cc.core.component.CCUtil;
import com.billy.cc.core.component.IComponent;

public class ComponentMessage implements IComponent {
    @Override
    public String getName() {
        return "ComponentMessage";

    }

    @Override
    public boolean onCall(CC cc) {
        String actionName = cc.getActionName();
        switch (actionName) {
            case "showMessageActivity":
                openMessageActivity(cc);
                break;
            default:
                break;
        }

        return false;
    }

    private void openMessageActivity(CC cc) {
        CCUtil.navigateTo(cc, MessageActivity.class);
        CC.sendCCResult(cc.getCallId(), CCResult.success());
    }
}
