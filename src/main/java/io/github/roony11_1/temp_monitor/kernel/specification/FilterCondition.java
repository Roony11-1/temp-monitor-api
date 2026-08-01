package io.github.roony11_1.temp_monitor.kernel.specification;

import java.util.Objects;

import lombok.Getter;

@Getter
public class FilterCondition 
{
    private final String field;
    private final FilterOperator operator;
    private final Object value;
    private final Object value2; // para BETWEEN
    
    public FilterCondition(String field, FilterOperator operator, Object value) 
    {
        this(field, operator, value, null);
    }
    
    public FilterCondition(String field, FilterOperator operator, Object value, Object value2) 
    {
        this.field = field;
        this.operator = operator;
        this.value = value;
        this.value2 = value2;
    }
    
    @Override
    public boolean equals(Object o) 
    {
        if (this == o) 
            return true;

        if (o == null || getClass() != o.getClass()) 
            return false;

        FilterCondition that = (FilterCondition) o;

        return Objects.equals(field, that.field) && 
               operator == that.operator && 
               Objects.equals(value, that.value) && 
               Objects.equals(value2, that.value2);
    }
    
    @Override
    public int hashCode() 
    {
        return Objects.hash(field, operator, value, value2);
    }
}