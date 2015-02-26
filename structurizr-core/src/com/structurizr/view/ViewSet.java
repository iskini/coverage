package com.structurizr.view;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.structurizr.model.Container;
import com.structurizr.model.Model;
import com.structurizr.model.SoftwareSystem;

import java.util.Collection;
import java.util.LinkedList;

/**
 * Represents a set of views onto a model.
 */
public class ViewSet {

    private Model model;

    private Collection<SystemContextView> systemContextViews = new LinkedList<>();
    private Collection<ContainerView> containerViews = new LinkedList<>();
    private Collection<ComponentView> componentViews = new LinkedList<>();

    private Styles styles = new Styles();

    ViewSet() {
    }

    public ViewSet(Model model) {
        this.model = model;
    }

    @JsonIgnore
    public Model getModel() {
        return model;
    }

    public void setModel(Model model) {
        this.model = model;
    }

    public SystemContextView createContextView(SoftwareSystem softwareSystem) {
        return createContextView(softwareSystem, null);
    }

    public SystemContextView createContextView(SoftwareSystem softwareSystem, String name) {
        SystemContextView view = new SystemContextView(softwareSystem, name);
        systemContextViews.add(view);

        return view;
    }

    public ContainerView createContainerView(SoftwareSystem softwareSystem) {
        return createContainerView(softwareSystem, null);
    }

    public ContainerView createContainerView(SoftwareSystem softwareSystem, String name) {
        ContainerView view = new ContainerView(softwareSystem, name);
        containerViews.add(view);

        return view;
    }

    public ComponentView createComponentView(Container container) {
        return createComponentView(container, null);
    }

    public ComponentView createComponentView(Container container, String name) {
        ComponentView view = new ComponentView(container, name);
        componentViews.add(view);

        return view;
    }

    public Collection<SystemContextView> getSystemContextViews() {
        return new LinkedList<>(systemContextViews);
    }

    public Collection<ContainerView> getContainerViews() {
        return new LinkedList<>(containerViews);
    }

    public Collection<ComponentView> getComponentViews() {
        return new LinkedList<>(componentViews);
    }

    public void hydrate() {
        systemContextViews.forEach(this::hydrateView);
        containerViews.forEach(this::hydrateView);
        componentViews.forEach(this::hydrateView);
        for (ComponentView view : componentViews) {
            hydrateView(view);
            view.setContainer(view.getSoftwareSystem().getContainerWithId(view.getContainerId()));
        }
    }

    private void hydrateView(View view) {
        view.setSoftwareSystem(model.getSoftwareSystemWithId(view.getSoftwareSystemId()));

        for (ElementView elementView : view.getElements()) {
            elementView.setElement(model.getElement(elementView.getId()));
        }
    }

    public Styles getStyles() {
        return styles;
    }

    public void copyLayoutInformationFrom(ViewSet source) {
        for (SystemContextView sourceView : source.getSystemContextViews()) {
            SystemContextView destinationView = findSystemContextView(sourceView);
            if (destinationView != null) {
                destinationView.copyLayoutInformationFrom(sourceView);
            }
        }

        for (ContainerView sourceView : source.getContainerViews()) {
            ContainerView destinationView = findContainerView(sourceView);
            if (destinationView != null) {
                destinationView.copyLayoutInformationFrom(sourceView);
            }
        }

        for (ComponentView sourceView : source.getComponentViews()) {
            ComponentView destinationView = findComponentView(sourceView);
            if (destinationView != null) {
                destinationView.copyLayoutInformationFrom(sourceView);
            }
        }
    }

    private SystemContextView findSystemContextView(SystemContextView systemContextView) {
        for (SystemContextView view : systemContextViews) {
            if (view.getTitle().equals(systemContextView.getTitle())) {
                return view;
            }
        }

        return null;
    }

    private ContainerView findContainerView(ContainerView containerView) {
        for (ContainerView view : containerViews) {
            if (view.getTitle().equals(containerView.getTitle())) {
                return view;
            }
        }

        return null;
    }

    private ComponentView findComponentView(ComponentView componentView) {
        for (ComponentView view : componentViews) {
            if (view.getTitle().equals(componentView.getTitle())) {
                return view;
            }
        }

        return null;
    }

}
