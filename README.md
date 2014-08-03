painter
=======

Automatic painter: reinterpret images via genetic algorithm

## Introduction

Painter automatically creates images toward recreating a target image from a set of any tile images.

## Try it

Unpack the test tileset with `tar zxvf tiles.tar.gz` in the `test` directory. Run `bin/test` and point your browser to http://localhost:8000. After the tileset loads, you can watch it work.

## Result

After 3500 generations: 

![Evolved image of the Mona Lisa]
(http://thinkdifferent.ly/stuff/painter-ml.png)

## Roll your own

```
mvn exec:java -Dexec.mainClass="painter.Painter" -Dexec.args="<target-image> <tile-directory> <tile-size>"
```

