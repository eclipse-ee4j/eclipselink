package org.eclipse.persistence.internal.jpa.parsing;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.internal.expressions.MapEntryExpression;
import org.eclipse.persistence.queries.ObjectLevelReadQuery;
import org.eclipse.persistence.queries.ReportQuery;

public class MapKeyNode extends Node {

    public MapKeyNode(){
        super();
    }
    
    /**
     * INTERNAL
     * Is this node a MapKey node
     */
    public boolean isMapKeyNode() {
        return true;
    }
    
    /**
     * INTERNAL
     * Apply this node to the passed query
     */
    public void applyToQuery(ObjectLevelReadQuery theQuery, GenerationContext generationContext) {
        ParseTreeContext context = generationContext.getParseTreeContext();
        if (theQuery instanceof ReportQuery) {
            ReportQuery reportQuery = (ReportQuery)theQuery;
            Expression expression = generateExpression(generationContext);
            reportQuery.addItem("MapKey", expression);
        }
    }
    
    /**
     * INTERNAL
     * Generate the a new EclipseLink TableEntryExpression for this node.
     */
    public Expression generateExpression(GenerationContext context) {
        Expression owningExpression = getLeft().generateExpression(context);
        MapEntryExpression whereClause = new MapEntryExpression(owningExpression);
        return whereClause;
    }
    
    /**
     * INTERNAL
     * Return the left most node of a dot expr, so return 'a' for 'a.b.c'.
     */
    public Node getLeftMostNode() {
        if (left.isDotNode()){
            return ((DotNode)left).getLeftMostNode();
        }
        return left;
    }
    
    public void validate(ParseTreeContext context) {
        TypeHelper typeHelper = context.getTypeHelper();
        left.validate(context);
        if (left.isVariableNode()){
            setType(((VariableNode)left).getTypeForMapKey(context));
        } else if (left.isDotNode()){
            setType(((DotNode)left).getTypeForMapKey(context));
        }
    }
}
