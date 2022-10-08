package Flipkart.injection;

import javax.ejb.Stateless;

@Stateless(name = "HelloBeanEJB")
public class HelloBean {
    public HelloBean() {
    }

    public String getNameType() {
        return "EJB";
    }
}
