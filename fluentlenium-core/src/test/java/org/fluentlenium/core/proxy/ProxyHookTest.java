package org.fluentlenium.core.proxy;

import com.google.common.base.Supplier;
import org.assertj.core.api.Assertions;
import org.fluentlenium.core.FluentControl;
import org.fluentlenium.core.components.ComponentInstantiator;
import org.fluentlenium.core.components.DefaultComponentInstantiator;
import org.fluentlenium.core.hook.BaseHook;
import org.fluentlenium.core.hook.DefaultHookChainBuilder;
import org.fluentlenium.core.hook.HookChainBuilder;
import org.fluentlenium.core.hook.HookDefinition;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.pagefactory.ElementLocator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class ProxyHookTest {
    @Mock
    private WebElement element1;

    @Mock
    private WebElement element2;

    @Mock
    private WebElement element3;

    @Mock
    private ElementLocator locator;

    @Mock
    private FluentControl fluentControl;


    private HookChainBuilder hookChainBuilder;

    private ComponentInstantiator componentInstantiator;

    @Before
    public void before() {
        componentInstantiator = new DefaultComponentInstantiator(fluentControl);
        hookChainBuilder = new DefaultHookChainBuilder(fluentControl, componentInstantiator);
    }

    public static class TestHook extends BaseHook<Object> {
        public TestHook(FluentControl fluentControl, ComponentInstantiator instantiator, Supplier<WebElement> elementSupplier, Supplier<ElementLocator> locatorSupplier, Object options) {
            super(fluentControl, instantiator, elementSupplier, locatorSupplier, options);
        }
    }

    @Test
    public void testHooksOnElement() {
        Mockito.when(locator.findElement()).thenReturn(element1);

        WebElement proxy = LocatorProxies.createWebElement(locator);
        LocatorProxies.now(proxy);

        List<HookDefinition<?>> hooks = new ArrayList<>();
        HookDefinition hookDefinition = new HookDefinition<>(TestHook.class);
        hooks.add(hookDefinition);

        ElementLocator hookLocator = LocatorProxies.getLocatorHandler(proxy).getHookLocator();
        WebElement hookElement = LocatorProxies.getLocatorHandler(proxy).getHookElement();

        Assertions.assertThat(hookLocator).isSameAs(locator);
        Assertions.assertThat(hookElement).isSameAs(element1);

        LocatorProxies.setHooks(proxy, hookChainBuilder, hooks);

        hookLocator = LocatorProxies.getLocatorHandler(proxy).getHookLocator();
        hookElement = LocatorProxies.getLocatorHandler(proxy).getHookElement();

        Assertions.assertThat(hookLocator).isExactlyInstanceOf(TestHook.class);
        Assertions.assertThat(hookElement).isExactlyInstanceOf(TestHook.class);
    }

    @Test
    public void testHooksOnElementList() {
        Mockito.when(locator.findElements()).thenReturn(Arrays.asList(element1, element2, element3));

        List<WebElement> proxy = LocatorProxies.createWebElementList(locator);
        LocatorProxies.now(proxy);

        List<HookDefinition<?>> hooks = new ArrayList<>();
        HookDefinition hookDefinition = new HookDefinition<>(TestHook.class);
        hooks.add(hookDefinition);

        ElementLocator hookLocator = LocatorProxies.getLocatorHandler(proxy).getHookLocator();
        WebElement hookElement = LocatorProxies.getLocatorHandler(proxy).getHookElement();

        Assertions.assertThat(hookLocator).isSameAs(locator);
        Assertions.assertThat(hookElement).isNull();

        LocatorProxies.setHooks(proxy, hookChainBuilder, hooks);

        hookLocator = LocatorProxies.getLocatorHandler(proxy).getHookLocator();
        hookElement = LocatorProxies.getLocatorHandler(proxy).getHookElement();

        Assertions.assertThat(hookLocator).isExactlyInstanceOf(TestHook.class);
        Assertions.assertThat(hookElement).isNull();
    }
}
