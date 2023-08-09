package org.civilian.controller.factory;


import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.HashSet;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import org.civilian.CivTest;
import org.junit.Test;


public class CdiControllerFactoryTest extends CivTest
{
	@Test public void test() throws Exception
	{
		BeanManager beanManager = mock(BeanManager.class);
		CdiControllerFactory factory = new CdiControllerFactory(beanManager);
		
		@SuppressWarnings("unchecked")
		Bean<String> bean = mock(Bean.class);
		HashSet<Bean<?>> beans = new HashSet<>();
		beans.add(bean);
		when(beanManager.getBeans(any(Type.class), any(Annotation[].class))).thenReturn(beans);
		when(beanManager.getReference(eq(bean), eq(String.class), any())).thenReturn("s");

		CdiController controller = (CdiController)factory.createController(CdiController.class);
		assertSame("s", controller.service);
	}
}
