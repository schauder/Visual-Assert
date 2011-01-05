Visual Assert is a small library for writing tests for the visual appearance of Swing components.

It does so basically by making a screenshot of the component and comparing it with an image stored in the classpath. 

There will be an interactive mode where the test displays a little dialog showing the component for manual .. err .. visual confirmation when it doesn't find a matching image in the classpath. 

On failed tests and on successful visual confirmation an image of the component is stored, wich can be used to 

a) understand the reason for the failing test
b) copy it in the classpath of your project so on the next run the test works without interaction with a human.