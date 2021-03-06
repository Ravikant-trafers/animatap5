package animata.model;

import java.io.File;
import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PImage;
import processing.xml.XMLElement;

import animata.model.Skeleton.Joint;
import animata.model.Skeleton.Bone;

public class Layer {

    public class Texture {

        private String location;
        private float x;
        private float y;
        private float scale;
        private PImage image;

        public Texture(XMLElement child, String folder) {
            location = folder + File.separator + child.getStringAttribute("location");
            x = child.getFloatAttribute("x");
            y = child.getFloatAttribute("y");
            scale = child.getFloatAttribute("scale");
        }

        public PImage getImage(PApplet applet) {
            if (image == null) {
                image = applet.loadImage(location);
            }
            return image;
        }
    }
    public ArrayList<Layer> layers = new ArrayList<Layer>();
    public Texture texture;
    public Mesh mesh;
    private Skeleton skeleton;
    public String name = "root";
    public float x = 0;
    public float y = 0;
    public float z = 0;
    public float alpha = 1;
    public float scale = 1;
    public boolean visible = true;

    public Layer() {
    }

    public Layer(XMLElement element, String folder) {
        setupAttributes(element);
        addChildLayersIfPresent(element, folder);
    }

    private void addChildLayersIfPresent(XMLElement element, String folder) {
        XMLElement[] innerLayers = element.getChildren("layer");
        if (innerLayers.length > 0) {
            addLayers(innerLayers, folder);
        } else {
            setupLayerContents(element, folder);
        }
    }

    private void setupAttributes(XMLElement element) {
        name = element.getStringAttribute("name","null");
        x = element.getFloatAttribute("x");
        y = element.getFloatAttribute("y");
        z = -element.getFloatAttribute("z");
        alpha = element.getFloatAttribute("alpha", 255);
        scale = element.getFloatAttribute("scale", 1);
        visible = element.getIntAttribute("vis") == 1;
    }

    private void setupLayerContents(XMLElement element, String folder) {
        texture = new Texture(element.getChild("texture"), folder);
        mesh = new Mesh(element.getChild("mesh"));
        XMLElement skeletonElement = element.getChild("skeleton");
        if (skeletonElement == null) {
            return;
        }
        skeleton = new Skeleton(skeletonElement, mesh);
    }

    private void addLayers(XMLElement[] children, String folder) {
        for (int i = 0; i < children.length; i++) {
            XMLElement element = children[i];
            addLayer(folder, element);
        }
    }

    public Layer addLayer(String folder, XMLElement element) {
        Layer layer = new Layer(element, folder);
        layers.add(layer);
        return layer;
    }

    public void simulate() {
        if (skeleton != null) {
            skeleton.simulate(40);
        }
        for (Layer layer : layers) {
            layer.simulate();
        }
    }

    public void moveJointX(String name, float x) {
        if (skeleton != null) {
            for (Joint joint : skeleton.allJoints) {
                if (joint.name.equals(name)) {
                    joint.x = x;
                }
            }
        }
        for (Layer llayer : layers) {
            llayer.moveJointX(name, x);
        }
    }

    public void moveJointY(String name, float y) {
        if (skeleton != null) {
            for (Joint joint : skeleton.allJoints) {
                if (joint.name.equals(name)) {
                    joint.y = y;
                }
            }
        }
        for (Layer llayer : layers) {
            llayer.moveJointY(name, y);
        }
    }

    public void setLayerAlpha(String _name, float a) {
        if (this != null) {
            if (this.name.equals(_name)) {
                this.alpha = a;
            }
        }
        for (Layer llayer : layers) {
            llayer.setLayerAlpha(_name, a);
        }
    }

    public void setLayerScale(String _name, float s) {
        if (this != null) {
            if (this.name.equals(_name)) {
                this.scale = s;
            }
        }
        for (Layer llayer : layers) {
            llayer.setLayerScale(_name, s);
        }
    }

    public void setLayerPos(String _name, float _x, float _y) {
        if (this != null) {
            if (this.name.equals(_name)) {
                x = _x;
                y = _y;
            }
        }
        for (Layer llayer : layers) {
            llayer.setLayerPos(_name, _x, _y);
        }
    }

    public void setBoneTempo(String name, float t) {
        if (skeleton != null) {
            for (Bone bone : skeleton.allBones) {
                if (bone.name.equals(name)) {
                    bone.tempo = t;
                }
            }
        }
        for (Layer llayer : layers) {
            llayer.setBoneTempo(name, t);
        }
    }

    public void setBoneRange(String name, float min, float max) {
        if (skeleton != null) {
            for (Bone bone : skeleton.allBones) {
                if (bone.name.equals(name)) {
                    bone.minScale = min;
                    bone.maxScale = max;
                }
            }
        }
        for (Layer llayer : layers) {
            llayer.setBoneRange(name, min, max);
        }
    }
}
