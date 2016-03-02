package com.interdev.game.screens.game.other;


import com.interdev.game.screens.game.WorldContactListener;

import java.util.ArrayList;

public class LabeledReferenceList extends ArrayList<LabeledReference> {

    public boolean contains(WorldContactListener.ContactLabels label) {
        for (LabeledReference labeledReference : this) {
            if (labeledReference.label.equals(label)) return true;
        }
        return false;
    }

    public <T> T getFirst(Class<T> tClass) {
        for (LabeledReference labeledReference : this) {
            if (labeledReference.reference != null && tClass.isAssignableFrom(labeledReference.reference.getClass())) {
                return (T) labeledReference.reference;
            }
        }
        return null;
    }

}
