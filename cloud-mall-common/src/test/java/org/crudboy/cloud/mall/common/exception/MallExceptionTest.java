package org.crudboy.cloud.mall.common.exception;

import org.junit.Test;

public class MallExceptionTest {

    @Test
    public void toJsonTest() {
        MallException mallException = new MallException(MallExceptionEnum.NEED_ADMIN);
        System.out.println(mallException);
    }
}
