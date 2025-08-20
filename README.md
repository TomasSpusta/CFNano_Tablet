# Nano tablet

Ceitec Nano Core Facility Tablet (Need better name)
A tablet represents another extension (link to the blueboxes GitHub) to the booking system (web address) at Ceitec Nano research facility. The main purpose of this extension is to provide the possibility to create a reservation on the spot, in the lab, only with a user/employee card for “small” instruments and/or the instruments which are used only for a short amount of time. Typical use cases are to use an optical microscope during metallography preparation or to use a fume hood for cleaning samples.  

The device is composed of a shelf tablet with USB-C port and additional charging contact on the side (pogo pins pads). The USB-C port is occupied with an RFID module, and the custom charging stand secures the power.
Tablet and RFID reader are encapsulated with the EVA foam protective holder (off-the-shelf product with some modification- link to wiki).  

The software of the tablet is a custom Android application programmed in the Kotlin language. The app provides a way to select an instrument (e.g. Fumehood), procedures which will be done on the instruments (etching and cleaning), the period of the reservation (e.g. 20 minutes), projects and samples connected with the reservation. With the optional/custom menus specific to the instrument, chemicals, and other laboratory hardware can be selected. 

