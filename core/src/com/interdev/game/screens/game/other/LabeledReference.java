package com.interdev.game.screens.game.other;

import com.interdev.game.screens.game.WorldContactListener;


public class LabeledReference {
    public WorldContactListener.ContactLabels label;
    public Object reference;

    public LabeledReference(WorldContactListener.ContactLabels label, Object reference) {
        this.label = label;
        this.reference = reference;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        if (obj.getClass().isEnum() && obj.equals(label)) return true;

        LabeledReference that = (LabeledReference) obj;

        if (label != that.label) return false;
        return reference.equals(that.reference);

    }

    @Override
    public int hashCode() {
        int result = label.hashCode();
        result = 31 * result + reference.hashCode();
        return result;
    }
}
