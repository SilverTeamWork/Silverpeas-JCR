The project goal is to wrap the implementation of the JCR for Silverpeas.
Currently, only Jackrabbit Oak is used as implementation.

By wrapping the implementation of the JCR, it allows to change it without 
having deep impact on the existing code of Silverpeas Core. This allows 
also to create a custom bridge between the Silverpeas world and the JCR
one. The bridge is for the JCR implementation to invoke some peculiar 
functions of Silverpeas. For instance, it's the autentication and 
authorization mechanism of Silverpeas instead of using thoses of the 
implementation itself.
