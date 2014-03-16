# JÖG

Built on top of the [LWJGL](http://lwjgl.org/), and heavily based upon [LÖVE](https://love2d.org/), JÖG is a game library that provides only what is necessary, and not too much more. It begun life using a few methods from [Slick](http://slick.ninjacave.com/), mainly those for audio, but I decided that needing slick just for audio meant that the user had access to all of slick, and so probably wouldn't need JÖG.

There is a list of things I'm planning to add in the [TODO](https://github.com/IMP1/jog/blob/master/TODO.md).

It is broken down into 8 modules: audio, filesystem, font, graphics, image, input, network, and window.

The **audio** module deals with music and sound effects. It uses OpenAL and currently accepts .ogg filetypes.

The **filesystem** module handles searching multiple locations for files, and can convert paths to URLs.

Currently the **font** module only handles bitmap fonts, and only very basically. This includes loading and drawing.

The **graphics** module deals with drawing things to the screen. It uses OpenGL ~~and currently relies on Slick for dealing with images~~. It features many methods for drawing simple shapes and lines, and for changing the colour of things drawn. It has different blend modes, including additive and subtractive, with the default being alpha. It can also rotate and translate drawn objects.

The **image** module loads images into OpenGL-friendly textures, using [Matthias Mann's PNG Decoder](http://hg.l33tlabs.org/twl/file/tip/src/de/matthiasmann/twl/utils/PNGDecoder.java), and allows access to the image's pixel data.

The **input** module allows the status of keyboard and mouse buttons (whether they're pressed or not), and has callbacks for when they are pressed and released.

The **network** module is being worked on currently and is very rough. I've just gotten multiple clients connecting and sending/recieving data, but it's very buggy and error-prone.

The **window** module allows creation of the window, and setting of certain settings, such as the title, icon and dimensions.

When I'm happy with the basic set of features (i.e when I feel 1.0.0 is done), I'll start working on a wiki.