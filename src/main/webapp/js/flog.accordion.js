var Dom = YAHOO.util.Dom;
var Event = YAHOO.util.Event;

YAHOO.namespace("flog");

YAHOO.flog.accordion = {
  init: function() {
    var accordion = document.getElementById("flog-accordion");
    var headers = accordion.getElementsByTagName("dt");

    Event.addListener(headers, "click", this.click);
  },

  click: function(e) {
    var header = this;

    if (Dom.hasClass(header, "selected")) {
      YAHOO.flog.accordion.collapse(header);
    } else {
      YAHOO.flog.accordion.expand(header);
    }
  },

  collapse: function(header) {
    Dom.removeClass(header,"selected");
    Dom.removeClass(Dom.getNextSibling(header), "open");
  },

  expand: function(header) {
    Dom.addClass(header,"selected");
    Dom.addClass(Dom.getNextSibling(header), "open");
  }
}

Event.on(window,"load", YAHOO.flog.accordion.init());
