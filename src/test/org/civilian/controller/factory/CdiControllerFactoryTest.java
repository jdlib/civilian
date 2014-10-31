package org.civilian.controller.factory;


import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyVararg;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.HashSet;
import javax.enterprise.context.spi.CreationalContext;
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
		when(beanManager.getBeans(any(Type.class), (Annotation[])anyVararg())).thenReturn(beans);
		when(beanManager.getReference(any(Bean.class), any(Type.class), any(CreationalContext.class))).thenReturn("s");

		CdiController controller = (CdiController)factory.createController(CdiController.class);
		assertSame("s", controller.service);
	}
}
