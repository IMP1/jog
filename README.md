# JÖG

### [Wiki (W.I.P)](https://github.com/IMP1/jog/wiki) | [License](https://github.com/IMP1/jog/blob/master/LICENSE.md) | [Issues](https://github.com/IMP1/jog/issues?state=open) 

Built on top of the [LWJGL](http://lwjgl.org/), and heavily based upon [LÖVE](https://love2d.org/), JÖG is a game library that provides only what is necessary, and not too much more. It begun life using a few methods from [Slick](http://slick.ninjacave.com/), mainly those for audio, but I decided that needing slick just for audio meant that the user had access to all of slick, and so probably wouldn't need JÖG.

It is broken down into the following 8 modules:

The [**audio**](https://github.com/IMP1/jog/wiki/jog.audio) module deals with music and sound effects. It uses [Kevin Glass's EasyOgg library](http://www.cokeandcode.com/) and currently accepts .ogg filetypes.

The [**filesystem**](https://github.com/IMP1/jog/wiki/jog.filesystem) module handles searching multiple locations for files, and can convert paths to URLs. It's mainly used internally to get absolute paths to resources so they can be used.

Currently the [**font**](https://github.com/IMP1/jog/wiki/jog.font) module only handles bitmap fonts, and only very basically. This includes loading and drawing.

The [**graphics**](https://github.com/IMP1/jog/wiki/jog.graphics) module uses OpenGL and deals with drawing things to the screen. It features many methods for drawing simple shapes and lines, and for changing the colour of things drawn. It has different blend modes, including additive and subtractive, with the default being alpha. It can also rotate, scale and translate drawn objects.

The [**image**](https://github.com/IMP1/jog/wiki/jog.image) module loads images into OpenGL-friendly textures, using [Matthias Mann's PNG Decoder](http://hg.l33tlabs.org/twl/file/tip/src/de/matthiasmann/twl/utils/PNGDecoder.java), and allows access to the image's pixel data.

The [**input**](https://github.com/IMP1/jog/wiki/jog.input) module allows the status of keyboard and mouse buttons (whether they're pressed or not), and has callbacks for when they are pressed and released.

The [**network**](https://github.com/IMP1/jog/wiki/jog.network) module is being worked on currently and is very rough. I've just gotten multiple clients connecting and sending/receiving data, but it's very buggy and error-prone.

The [**window**](https://github.com/IMP1/jog/wiki/jog.window) module allows creation of the window, and setting of certain settings, such as the title, icon and dimensions. It features fullscreen and/or borderless windows, as well as your default windowed mode.

I've started on a wiki, but very little is there, and what is there is subject to change.

If you have any suggestions or comments, you can email me at IMP1@mail.com.