/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
var rave = rave || {};
rave.opensocial = rave.opensocial || (function() {
    var WIDGET_TYPE = "OpenSocial";
    var OFFSET = 10;
    var MIN_HEIGHT = 250;

    var container;

    /**
     * Initialization
     */
    function initOpenSocial() {
        initContainer();
        registerRpcHooks();
    }

    function initContainer() {
        //Create the common container instance.
        var containerConfig = {};
        containerConfig[osapi.container.ServiceConfig.API_PATH] = "/rpc";
        containerConfig[osapi.container.ContainerConfig.RENDER_DEBUG] = "1";
        container = new osapi.container.Container(containerConfig);
    }

    /**
     * Gets the container singleton or initializes if not instantiated
     */
    function getContainer() {
        if (!container) {
            initContainer();
        }
        return container;
    }

    /** Resets the current singleton reference while allowing anything with a handle on the
     *  current singleton to use that instance.
     */
    function resetContainer() {
        container = null;
    }

    /**
     * Registers the RPC hooks with the container
     */
    function registerRpcHooks() {
        container.rpcRegister('resize_iframe', resizeIframe);
        container.rpcRegister('set_title', setTitle);
        container.rpcRegister('requestNavigateTo', requestNavigateTo);
        //container.rpcRegister('set_pref', null);
        //container.rpcRegister('pubsub', null);
    }

    /**
     * Validates a gadget's metadata and renders it on the page
     *
     * @param gadget the gadget object to be rendered by the container
     */
    function validateAndRenderGadget(gadget) {
        var validationResult = validateMetadata(gadget.metadata);
        if (validationResult.valid) {
            //TODO: Submit a patch to Shindig common container to expose the backing service or add a method to push cached items  into the container config
            var commonContainerMetadataWrapper = {};
            commonContainerMetadataWrapper[gadget.widgetUrl] = gadget.metadata;
            container.service_.addGadgetMetadatas(commonContainerMetadataWrapper, null);
            renderNewGadget(gadget);
        } else {
            rave.errorWidget(gadget.regionWidgetId, "Unable to render OpenSocial Gadget: <br /><br />" + validationResult.error);
        }

    }

    /**
     * Renders a new gadget
     * @param gadget
     */
    function renderNewGadget(gadget) {
        var widgetBodyElement = document.getElementById(["widget-", gadget.regionWidgetId, "-body"].join(""));
        gadget.site = container.newGadgetSite(widgetBodyElement);
        gadget.maximize = function() { renderGadgetView("canvas", this); };
        gadget.minimize = function() { renderGadgetView("home",   this); };
        renderGadgetView("home", gadget);
    }

    /**
     * Renders a gadget in the given view;
     * @param view the view to render
     * @param gadget the Rave widget object
     */
    function renderGadgetView(view, gadget) {
        var renderParams = {};
        var size = getSizeFromElement(gadget.regionWidgetId, view);
        renderParams[osapi.container.RenderParam.VIEW] = view;
        renderParams[osapi.container.RenderParam.WIDTH] = size.width;
        renderParams[osapi.container.RenderParam.HEIGHT] = size.height;
        renderParams[osapi.container.RenderParam.USER_PREFS] = getCompleteUserPrefSet(gadget.userPrefs, gadget.metadata.userPrefs);
        container.navigateGadget(gadget.site, gadget.widgetUrl, {}, renderParams);
    }

    function getSizeFromElement(id, view) {
        var elem = document.getElementById("widget-" + id + "-wrapper");
        return {width: elem.clientWidth - OFFSET, height: view == "canvas" ? elem.clientHeight : MIN_HEIGHT};
    }

    /**
     * validates the metadata for the current gadget
     * @param metadata the metadata object to validate
     */
    function validateMetadata(metadata) {
        if(typeof metadata.error != "undefined") {
            return {valid:false, error:metadata.error.message};
        }

        return {valid:true};
    }

    /**
     * Combines the default user pref list from the metadata with those set by the user
     * @param setPrefs preferences already set by the user
     * @param metadataPrefs list of all available metadata objects
     */
    function getCompleteUserPrefSet(setPrefs, metadataPrefs) {
        var combined = {};
        for (var key in metadataPrefs) {
            var metaPref = metadataPrefs[key];
            var userPref = setPrefs[metaPref.name];
            combined[metaPref.name] = typeof userPref == "undefined" ? metaPref.defaultValue : userPref;
        }
        return combined;
    }

    /*
     RPC Callback handlers
     */
    /**
     * Resizes the iFrame when gadgets.window.adjustHeight is called
     *
     * @param args the RPC event args
     */
    function resizeIframe(args) {
        var max = 0x7FFFFFFF;
        var height = args.a > max ? max : args.a;
        args.gs.setHeight(height);
    }

    /**
     * Sets the chrome title when gadgets.window.setTitle is caled
     *
     * @param args RPC event args
     */
    function setTitle(args) {

        //TODO: This implementation relies on parsing of the gadgetHolder's element id
        //to retrieve the module ID
        //A patch should be submitted to Shindig's common container code to properly
        //set the iFrame ID to the module id
        var bodyId = args.gs.getActiveGadgetHolder().getElement().id;
        var titleId = "widget-" + rave.getObjectIdFromDomId(bodyId) + "-title";
        var element = document.getElementById(titleId);
        if (element) {
            var a = isArray(args.a) ? args.a[0] : args.a;
            element.innerHTML = gadgets.util.escapeString(a);
        }

    }

    /**
     * Re-renders the gadget in the requested view with the parameters
     *
     * @param view target view
     * @param opt_params
     */
    function requestNavigateTo(view, opt_params) {
        //TODO: Implement this function
        throw "Not Implemented!!!!!";
    }

    /**
     * Utility functions
     */
    function isArray(o) {
        return Object.prototype.toString.call(o) == "[object Array]";
    }

    /**
     * Exposed public API calls
     */
    return {
        TYPE : WIDGET_TYPE,
        /**
         * Initializes the Rave OpenSocial machinery
         */
        init : initOpenSocial,
        /**
         * Gets a reference to the container singleton
         */
        container: getContainer,
        /**
         * Instantiates and renders the given gadget
         * @param a gadget to render
         */
        initWidget: validateAndRenderGadget,

        /**
         * Resets the current OpenSocial container
         */
        reset: resetContainer
    };

})();

//Register the widget provider with Rave
rave.registerProvider(rave.opensocial);