package com.jade.physics2d;

import com.jade.GameObject;
import com.jade.physics2d.primitives.Collider2D;

public class QuadTreeData {
    private GameObject go;
    private Collider2D collider;
    private boolean flag;

    public QuadTreeData() {
        this.go = null;
        this.collider = null;
        this.flag = false;
    }

    public QuadTreeData(GameObject go) {
        this.go = go;
        this.collider = go.getComponent(Collider2D.class);
    }

    public void setGameObject(GameObject go) {
        this.go = go;
    }

    public void setCollider(Collider2D coll) {
        this.collider = coll;
    }

    public GameObject gameObject() {
        return this.go;
    }

    public Collider2D collider() {
        return this.collider;
    }

    public void setFlag(boolean val) {
        this.flag = val;
    }

    public boolean flag() {
        return this.flag;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) return false;
        if (!(other instanceof QuadTreeData)) return false;

        QuadTreeData data = (QuadTreeData)other;
        return data.gameObject().equals(this.go);
    }
}
