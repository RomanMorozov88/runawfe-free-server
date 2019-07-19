package ru.runa.wfe.lang;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Map;
import ru.runa.wfe.commons.Utils;
import ru.runa.wfe.execution.ExecutionContext;
import ru.runa.wfe.var.VariableMapping;

public class BaseReceiveMessageNode extends BaseMessageNode implements BoundaryEventContainer {
    private static final long serialVersionUID = 1L;
    private final List<BoundaryEvent> boundaryEvents = Lists.newArrayList();

    @Override
    public NodeType getNodeType() {
        return NodeType.RECEIVE_MESSAGE;
    }

    @Override
    protected void execute(ExecutionContext executionContext) throws Exception {
        executionContext.getToken().setMessageSelector(Utils.getReceiveMessageNodeSelector(executionContext.getVariableProvider(), this));
    }

    public void leave(ExecutionContext executionContext, Map<String, Object> map) {
        for (VariableMapping variableMapping : getVariableMappings()) {
            if (!variableMapping.isPropertySelector()) {
                if (map.containsKey(variableMapping.getMappedName())) {
                    Object value = map.get(variableMapping.getMappedName());
                    executionContext.setVariableValue(variableMapping.getName(), value);
                } else {
                    log.warn("message does not contain value for '" + variableMapping.getMappedName() + "'");
                }
            }
        }
        super.leave(executionContext);
    }

    @Override
    public void leave(ExecutionContext executionContext, Transition transition) {
        super.leave(executionContext, transition);
        executionContext.getToken().setMessageSelector(null);
    }

    @Override
    public List<BoundaryEvent> getBoundaryEvents() {
        return boundaryEvents;
    }

}
