# JÖG

Built on top of the [LWJGL](http://lwjgl.org/), and heavily based upon [LÖVE](https://love2d.org/), JÖG is a game library that provides only what is necessary, and not too much more.

## <a name="audio"></a> audio
### Inplemented
The audio module deals with music and sound effects. It uses OpenAL and currently accepts .ogg filetypes.
### To Do:
  - Remove depenencies on Slick*
    - Add a way to load music files
  - Add .mp3 and .wav support


## <a name="filesystem"></a> filesystem
### Inplemented
Very little.
### To Do:
  - convert filepaths to find files within project/appdata folder.
  - load files from within project/appdata folder.
 

## <a name="graphics"></a> graphics
### Inplemented
The graphics module deals with drawing things to the screen. It uses OpenGL and currently relies on Slick for dealing with images. It features many methods for drawing simple shapes and lines, and for changing the colour of things drawn. It has different blend modes, including additive and subtractive, with the default being alpha. It can also rotate and translate drawn objects.
### To Do:
  - remove dependencies on Slick*
    - move image loading and handling into [jog.image](#image)
    - create image loading and OpenGL texture binding


## <a name="image"></a> image
### Inplemented
Very little.
### To Do:
  - load images (using [jog.filesystem](#filesystem))
  - bind image textures for OpenGL


## <a name="input"></a> input
### Inplemented
Allows the status of keyboard and mouse buttons (whether they're pressed or not), and has callbacks for when they are pressed and released.
### To Do:
  - Add gamepad support
  - Maybe add touch support (not sure how this works on PCs)


## <a name="network"></a> network
### Inplemented
Nothing yet.
### To Do:
  - Add server and client creation
  - Add communication
  - Add callbacks for connections, disconnections and messages


## <a name="window"></a> window
### Inplemented
Allows creation of the window, and setting of certain settings, such as the title, icon and dimensions.
### To Do:
  - Add checking of window is currently in focus
  - Maybe allow for window resizing
  - Add callbacks for window resizing, focus being gained/lost


*****


\* I have no problem with [Slick](http://slick.ninjacave.com/) (It's a great library full of features), I'd just rather have JÖG not rely on a library that it only uses for a couple of things.