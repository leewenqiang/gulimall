package com.atguigu.common.valid;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.HashSet;
import java.util.Set;

/**
 * @ClassName ListValueConstraintValidator
 * @Description TODO
 * @Author lwq
 * @Date 2020/12/19 15:52
 * @Version 1.0
 */
public class ListValueConstraintValidator implements ConstraintValidator<ListValue,Integer> {


    Set<Integer> set = new HashSet<>();

    @Override
    public void initialize(ListValue constraintAnnotation) {
        int[] vaus = constraintAnnotation.vals();
        for (int vau : vaus) {
            set.add(vau);
        }
    }

    @Override
    public boolean isValid(Integer integer, ConstraintValidatorContext constraintValidatorContext) {
        return set.contains(integer);
    }
}
