module e.ink.display.main {
    requires org.slf4j;
    requires gpio.main;

    exports org.digitalsmile.display;
    exports org.digitalsmile.display.controllers;
    exports org.digitalsmile.display.color;
}