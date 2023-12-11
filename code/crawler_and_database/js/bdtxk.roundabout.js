(function(a) {
    "use strict";
    var b, c, d;
    a.extend({
        roundaboutShapes: {
            def: "lazySusan",
            lazySusan: function(a, b, c) {
                return {
                    x: Math.sin(a + b),
                    y: Math.sin(a + 3 * Math.PI / 2 + b) / 8 * c,
                    z: (Math.cos(a + b) + 1) / 2,
                    scale: Math.sin(a + Math.PI / 2 + b) / 2 + .5
                }
            }
        }
    });
    b = {
        bearing: 0,
        tilt: 0,
        minZ: 100,
        maxZ: 280,
        minOpacity: .4,
        maxOpacity: 1,
        minScale: .4,
        maxScale: 1,
        duration: 600,
        btnNext: null,
        btnNextCallback: function() {},
        btnPrev: null,
        btnPrevCallback: function() {},
        btnToggleAutoplay: null,
        btnStartAutoplay: null,
        btnStopAutoplay: null,
        easing: "swing",
        clickToFocus: true,
        clickToFocusCallback: function() {},
        focusBearing: 0,
        shape: "lazySusan",
        debug: false,
        childSelector: "li",
        startingChild: null,
        reflect: false,
        floatComparisonThreshold: .001,
        autoplay: false,
        autoplayDuration: 1e3,
        autoplayPauseOnHover: false,
        autoplayCallback: function() {},
        autoplayInitialDelay: 0,
        enableDrag: false,
        dropDuration: 600,
        dropEasing: "swing",
        dropAnimateTo: "nearest",
        dropCallback: function() {},
        dragAxis: "x",
        dragFactor: 4,
        triggerFocusEvents: true,
        triggerBlurEvents: true,
        responsive: false
    };
    c = {
        autoplayInterval: null,
        autoplayIsRunning: false,
        autoplayStartTimeout: null,
        animating: false,
        childInFocus: -1,
        touchMoveStartPosition: null,
        stopAnimation: false,
        lastAnimationStep: false
    };
    d = {
        init: function(e, f, g) {
            var h, i = (new Date).getTime();
            e = typeof e === "object" ? e : {};
            f = a.isFunction(f) ? f : function() {};
            f = a.isFunction(e) ? e : f;
            h = a.extend({}, b, e, c);
            return this.each(function() {
                var b = a(this),
                    c = b.children(h.childSelector).length,
                    e = 360 / c,
                    i = h.startingChild && h.startingChild > c - 1 ? c - 1 : h.startingChild,
                    j = h.startingChild === null ? h.bearing : 360 - i * e,
                    k = b.css("position") !== "static" ? b.css("position") : "relative";
                b.css({
                    padding: 0,
                    position: k
                }).addClass("roundabout-holder").data("roundabout", a.extend({}, h, {
                    startingChild: i,
                    bearing: j,
                    oppositeOfFocusBearing: d.normalize.apply(null, [h.focusBearing - 180]),
                    dragBearing: j,
                    period: e
                }));
                if (g) {
                    b.unbind(".roundabout").children(h.childSelector).unbind(".roundabout")
                } else {
                    if (h.responsive) {
                        a(window).bind("resize", function() {
                            d.stopAutoplay.apply(b);
                            d.relayoutChildren.apply(b)
                        })
                    }
                }
                if (h.clickToFocus) {
                    b.children(h.childSelector).each(function(c) {
                        a(this).bind("click.roundabout", function() {
                            var e = d.getPlacement.apply(b, [c]);
                            if (!d.isInFocus.apply(b, [e])) {
                                d.stopAnimation.apply(a(this));
                                if (!b.data("roundabout").animating) {
                                    d.animateBearingToFocus.apply(b, [e, b.data("roundabout").clickToFocusCallback])
                                }
                                return false
                            }
                        })
                    })
                }
                if (h.btnNext) {
                    a(h.btnNext).bind("click.roundabout", function() {
                        if (!b.data("roundabout").animating) {
                            d.animateToNextChild.apply(b, [b.data("roundabout").btnNextCallback])
                        }
                        return false
                    })
                }
                if (h.btnPrev) {
                    a(h.btnPrev).bind("click.roundabout", function() {
                        d.animateToPreviousChild.apply(b, [b.data("roundabout").btnPrevCallback]);
                        return false
                    })
                }
                if (h.btnToggleAutoplay) {
                    a(h.btnToggleAutoplay).bind("click.roundabout", function() {
                        d.toggleAutoplay.apply(b);
                        return false
                    })
                }
                if (h.btnStartAutoplay) {
                    a(h.btnStartAutoplay).bind("click.roundabout", function() {
                        d.startAutoplay.apply(b);
                        return false
                    })
                }
                if (h.btnStopAutoplay) {
                    a(h.btnStopAutoplay).bind("click.roundabout", function() {
                        d.stopAutoplay.apply(b);
                        return false
                    })
                }
                if (h.autoplayPauseOnHover) {
                    b.bind("mouseenter.roundabout.autoplay", function() {
                        d.stopAutoplay.apply(b, [true])
                    }).bind("mouseleave.roundabout.autoplay", function() {
                        d.startAutoplay.apply(b)
                    })
                }
                if (h.enableDrag) {
                    if (!a.isFunction(b.drag)) {
                        if (h.debug) {
                            alert("You do not have the drag plugin loaded.")
                        }
                    } else if (!a.isFunction(b.drop)) {
                        if (h.debug) {
                            alert("You do not have the drop plugin loaded.")
                        }
                    } else {
                        b.drag(function(a, c) {
                            var e = b.data("roundabout"),
                                f = e.dragAxis.toLowerCase() === "x" ? "deltaX" : "deltaY";
                            d.stopAnimation.apply(b);
                            d.setBearing.apply(b, [e.dragBearing + c[f] / e.dragFactor])
                        }).drop(function(a) {
                            var c = b.data("roundabout"),
                                e = d.getAnimateToMethod(c.dropAnimateTo);
                            d.allowAnimation.apply(b);
                            d[e].apply(b, [c.dropDuration, c.dropEasing, c.dropCallback]);
                            c.dragBearing = c.period * d.getNearestChild.apply(b)
                        })
                    }
                    b.each(function() {
                        var b = a(this).get(0),
                            c = a(this).data("roundabout"),
                            e = c.dragAxis.toLowerCase() === "x" ? "pageX" : "pageY",
                            f = d.getAnimateToMethod(c.dropAnimateTo);
                        if (b.addEventListener) {
                            b.addEventListener("touchstart", function(a) {
                                c.touchMoveStartPosition = a.touches[0][e]
                            }, false);
                            b.addEventListener("touchmove", function(b) {
                                var f = (b.touches[0][e] - c.touchMoveStartPosition) / c.dragFactor;
                                b.preventDefault();
                                d.stopAnimation.apply(a(this));
                                d.setBearing.apply(a(this), [c.dragBearing + f])
                            }, false);
                            b.addEventListener("touchend", function(b) {
                                b.preventDefault();
                                d.allowAnimation.apply(a(this));
                                f = d.getAnimateToMethod(c.dropAnimateTo);
                                d[f].apply(a(this), [c.dropDuration, c.dropEasing, c.dropCallback]);
                                c.dragBearing = c.period * d.getNearestChild.apply(a(this))
                            }, false)
                        }
                    })
                }
                d.initChildren.apply(b, [f, g])
            })
        },
        initChildren: function(b, c) {
            var e = a(this),
                f = e.data("roundabout");
            b = b || function() {};
            e.children(f.childSelector).each(function(b) {
                var f, g, h, i = d.getPlacement.apply(e, [b]);
                if (c && a(this).data("roundabout")) {
                    f = a(this).data("roundabout").startWidth;
                    g = a(this).data("roundabout").startHeight;
                    h = a(this).data("roundabout").startFontSize
                }
                a(this).addClass("roundabout-moveable-item").css("position", "absolute");
                a(this).data("roundabout", {
                    startWidth: f || a(this).width(),
                    startHeight: g || a(this).height(),
                    startFontSize: h || parseInt(a(this).css("font-size"), 10),
                    degrees: i,
                    backDegrees: d.normalize.apply(null, [i - 180]),
                    childNumber: b,
                    currentScale: 1,
                    parent: e
                })
            });
            d.updateChildren.apply(e);
            if (f.autoplay) {
                f.autoplayStartTimeout = setTimeout(function() {
                    d.startAutoplay.apply(e)
                }, f.autoplayInitialDelay)
            }
            e.trigger("ready");
            b.apply(e);
            return e
        },
        updateChildren: function() {
            return this.each(function() {
                var b = a(this),
                    c = b.data("roundabout"),
                    e = -1,
                    f = {
                        bearing: c.bearing,
                        tilt: c.tilt,
                        stage: {
                            width: Math.floor(a(this).width() * .9),
                            height: Math.floor(a(this).height() * .9)
                        },
                        animating: c.animating,
                        inFocus: c.childInFocus,
                        focusBearingRadian: d.degToRad.apply(null, [c.focusBearing]),
                        shape: a.roundaboutShapes[c.shape] || a.roundaboutShapes[a.roundaboutShapes.def]
                    };
                f.midStage = {
                    width: f.stage.width / 2,
                    height: f.stage.height / 2
                };
                f.nudge = {
                    width: f.midStage.width + f.stage.width * .05,
                    height: f.midStage.height + f.stage.height * .05
                };
                f.zValues = {
                    min: c.minZ,
                    max: c.maxZ,
                    diff: c.maxZ - c.minZ
                };
                f.opacity = {
                    min: c.minOpacity,
                    max: c.maxOpacity,
                    diff: c.maxOpacity - c.minOpacity
                };
                f.scale = {
                    min: c.minScale,
                    max: c.maxScale,
                    diff: c.maxScale - c.minScale
                };
                b.children(c.childSelector).each(function(g) {
                    if (d.updateChild.apply(b, [a(this), f, g, function() {
                            a(this).trigger("ready")
                        }]) && (!f.animating || c.lastAnimationStep)) {
                        e = g;
                        a(this).addClass("roundabout-in-focus")
                    } else {
                        a(this).removeClass("roundabout-in-focus")
                    }
                });
                if (e !== f.inFocus) {
                    if (c.triggerBlurEvents) {
                        b.children(c.childSelector).eq(f.inFocus).trigger("blur")
                    }
                    c.childInFocus = e;
                    if (c.triggerFocusEvents && e !== -1) {
                        b.children(c.childSelector).eq(e).trigger("focus")
                    }
                }
                b.trigger("childrenUpdated")
            })
        },
        updateChild: function(b, c, e, f) {
            var g, h = this,
                i = a(b),
                j = i.data("roundabout"),
                k = [],
                l = d.degToRad.apply(null, [360 - j.degrees + c.bearing]);
            f = f || function() {};
            l = d.normalizeRad.apply(null, [l]);
            g = c.shape(l, c.focusBearingRadian, c.tilt);
            g.scale = g.scale > 1 ? 1 : g.scale;
            g.adjustedScale = (c.scale.min + c.scale.diff * g.scale).toFixed(4);
            g.width = (g.adjustedScale * j.startWidth).toFixed(4);
            g.height = (g.adjustedScale * j.startHeight).toFixed(4);
            i.css({
                left: (g.x * c.midStage.width + c.nudge.width - g.width / 2).toFixed(0) + "px",
                top: (g.y * c.midStage.height + c.nudge.height - g.height / 2).toFixed(0) + "px",
                width: g.width + "px",
                height: g.height + "px",
                opacity: (c.opacity.min + c.opacity.diff * g.scale).toFixed(2),
                zIndex: Math.round(c.zValues.min + c.zValues.diff * g.z),
                fontSize: (g.adjustedScale * j.startFontSize).toFixed(1) + "px"
            });
            j.currentScale = g.adjustedScale;
            if (h.data("roundabout").debug) {
                k.push('<div style="font-weight: normal; font-size: 10px; padding: 2px; width: ' + i.css("width") + '; background-color: #ffc;">');
                k.push('<strong style="font-size: 12px; white-space: nowrap;">Child ' + e + "</strong><br />");
                k.push("<strong>left:</strong> " + i.css("left") + "<br />");
                k.push("<strong>top:</strong> " + i.css("top") + "<br />");
                k.push("<strong>width:</strong> " + i.css("width") + "<br />");
                k.push("<strong>opacity:</strong> " + i.css("opacity") + "<br />");
                k.push("<strong>height:</strong> " + i.css("height") + "<br />");
                k.push("<strong>z-index:</strong> " + i.css("z-index") + "<br />");
                k.push("<strong>font-size:</strong> " + i.css("font-size") + "<br />");
                k.push("<strong>scale:</strong> " + i.data("roundabout").currentScale);
                k.push("</div>");
                i.html(k.join(""))
            }
            i.trigger("reposition");
            f.apply(h);
            return d.isInFocus.apply(h, [j.degrees])
        },
        setBearing: function(b, c) {
            c = c || function() {};
            b = d.normalize.apply(null, [b]);
            this.each(function() {
                var c, e, f, g = a(this),
                    h = g.data("roundabout"),
                    i = h.bearing;
                h.bearing = b;
                g.trigger("bearingSet");
                d.updateChildren.apply(g);
                c = Math.abs(i - b);
                if (!h.animating || c > 180) {
                    return
                }
                c = Math.abs(i - b);
                g.children(h.childSelector).each(function(c) {
                    var e;
                    if (d.isChildBackDegreesBetween.apply(a(this), [b, i])) {
                        e = i > b ? "Clockwise" : "Counterclockwise";
                        a(this).trigger("move" + e + "ThroughBack")
                    }
                })
            });
            c.apply(this);
            return this
        },
        adjustBearing: function(b, c) {
            c = c || function() {};
            if (b === 0) {
                return this
            }
            this.each(function() {
                d.setBearing.apply(a(this), [a(this).data("roundabout").bearing + b])
            });
            c.apply(this);
            return this
        },
        setTilt: function(b, c) {
            c = c || function() {};
            this.each(function() {
                a(this).data("roundabout").tilt = b;
                d.updateChildren.apply(a(this))
            });
            c.apply(this);
            return this
        },
        adjustTilt: function(b, c) {
            c = c || function() {};
            this.each(function() {
                d.setTilt.apply(a(this), [a(this).data("roundabout").tilt + b])
            });
            c.apply(this);
            return this
        },
        animateToBearing: function(b, c, e, f, g) {
            var h = (new Date).getTime();
            g = g || function() {};
            if (a.isFunction(f)) {
                g = f;
                f = null
            } else if (a.isFunction(e)) {
                g = e;
                e = null
            } else if (a.isFunction(c)) {
                g = c;
                c = null
            }
            this.each(function() {
                var i, j, k, l = a(this),
                    m = l.data("roundabout"),
                    n = !c ? m.duration : c,
                    o = e ? e : m.easing || "swing";
                if (!f) {
                    f = {
                        timerStart: h,
                        start: m.bearing,
                        totalTime: n
                    }
                }
                i = h - f.timerStart;
                if (m.stopAnimation) {
                    d.allowAnimation.apply(l);
                    m.animating = false;
                    return
                }
                if (i < n) {
                    if (!m.animating) {
                        l.trigger("animationStart")
                    }
                    m.animating = true;
                    if (typeof a.easing.def === "string") {
                        j = a.easing[o] || a.easing[a.easing.def];
                        k = j(null, i, f.start, b - f.start, f.totalTime)
                    } else {
                        k = a.easing[o](i / f.totalTime, i, f.start, b - f.start, f.totalTime)
                    }
                    if (d.compareVersions.apply(null, [a().jquery, "x.x.x"]) >= 0 && !a.easing["easeOutBack"]) {
                        k = f.start + (b - f.start) * k
                    }
                    k = d.normalize.apply(null, [k]);
                    m.dragBearing = k;
                    d.setBearing.apply(l, [k, function() {
                        setTimeout(function() {
                            d.animateToBearing.apply(l, [b, n, o, f, g])
                        }, 0)
                    }])
                } else {
                    m.lastAnimationStep = true;
                    b = d.normalize.apply(null, [b]);
                    d.setBearing.apply(l, [b, function() {
                        l.trigger("animationEnd")
                    }]);
                    m.animating = false;
                    m.lastAnimationStep = false;
                    m.dragBearing = b;
                    g.apply(l)
                }
            });
            return this
        },
        animateToNearbyChild: function(b, c) {
            var e = b[0],
                f = b[1],
                g = b[2] || function() {};
            if (a.isFunction(f)) {
                g = f;
                f = null
            } else if (a.isFunction(e)) {
                g = e;
                e = null
            }
            return this.each(function() {
                var b, h, i = a(this),
                    j = i.data("roundabout"),
                    k = !j.reflect ? j.bearing % 360 : j.bearing,
                    l = i.children(j.childSelector).length;
                if (!j.animating) {
                    if (j.reflect && c === "previous" || !j.reflect && c === "next") {
                        k = Math.abs(k) < j.floatComparisonThreshold ? 360 : k;
                        for (b = 0; b < l; b += 1) {
                            h = {
                                lower: j.period * b,
                                upper: j.period * (b + 1)
                            };
                            h.upper = b === l - 1 ? 360 : h.upper;
                            if (k <= Math.ceil(h.upper) && k >= Math.floor(h.lower)) {
                                if (l === 2 && k === 360) {
                                    d.animateToDelta.apply(i, [-180, e, f, g])
                                } else {
                                    d.animateBearingToFocus.apply(i, [h.lower, e, f, g])
                                }
                                break
                            }
                        }
                    } else {
                        k = Math.abs(k) < j.floatComparisonThreshold || 360 - Math.abs(k) < j.floatComparisonThreshold ? 0 : k;
                        for (b = l - 1; b >= 0; b -= 1) {
                            h = {
                                lower: j.period * b,
                                upper: j.period * (b + 1)
                            };
                            h.upper = b === l - 1 ? 360 : h.upper;
                            if (k >= Math.floor(h.lower) && k < Math.ceil(h.upper)) {
                                if (l === 2 && k === 360) {
                                    d.animateToDelta.apply(i, [180, e, f, g])
                                } else {
                                    d.animateBearingToFocus.apply(i, [h.upper, e, f, g])
                                }
                                break
                            }
                        }
                    }
                }
            })
        },
        animateToNearestChild: function(b, c, e) {
            e = e || function() {};
            if (a.isFunction(c)) {
                e = c;
                c = null
            } else if (a.isFunction(b)) {
                e = b;
                b = null
            }
            return this.each(function() {
                var f = d.getNearestChild.apply(a(this));
                d.animateToChild.apply(a(this), [f, b, c, e])
            })
        },
        animateToChild: function(b, c, e, f) {
            f = f || function() {};
            if (a.isFunction(e)) {
                f = e;
                e = null
            } else if (a.isFunction(c)) {
                f = c;
                c = null
            }
            return this.each(function() {
                var g, h = a(this),
                    i = h.data("roundabout");
                if (i.childInFocus !== b && !i.animating) {
                    g = h.children(i.childSelector).eq(b);
                    d.animateBearingToFocus.apply(h, [g.data("roundabout").degrees, c, e, f])
                }
            })
        },
        animateToNextChild: function(a, b, c) {
            return d.animateToNearbyChild.apply(this, [arguments, "next"])
        },
        animateToPreviousChild: function(a, b, c) {
            return d.animateToNearbyChild.apply(this, [arguments, "previous"])
        },
        animateToDelta: function(b, c, e, f) {
            f = f || function() {};
            if (a.isFunction(e)) {
                f = e;
                e = null
            } else if (a.isFunction(c)) {
                f = c;
                c = null
            }
            return this.each(function() {
                var g = a(this).data("roundabout").bearing + b;
                d.animateToBearing.apply(a(this), [g, c, e, f])
            })
        },
        animateBearingToFocus: function(b, c, e, f) {
            f = f || function() {};
            if (a.isFunction(e)) {
                f = e;
                e = null
            } else if (a.isFunction(c)) {
                f = c;
                c = null
            }
            return this.each(function() {
                var g = a(this).data("roundabout").bearing - b;
                g = Math.abs(360 - g) < Math.abs(g) ? 360 - g : -g;
                g = g > 180 ? -(360 - g) : g;
                if (g !== 0) {
                    d.animateToDelta.apply(a(this), [g, c, e, f])
                }
            })
        },
        stopAnimation: function() {
            return this.each(function() {
                a(this).data("roundabout").stopAnimation = true
            })
        },
        allowAnimation: function() {
            return this.each(function() {
                a(this).data("roundabout").stopAnimation = false
            })
        },
        startAutoplay: function(b) {
            return this.each(function() {
                var c = a(this),
                    e = c.data("roundabout");
                b = b || e.autoplayCallback || function() {};
                clearInterval(e.autoplayInterval);
                e.autoplayInterval = setInterval(function() {
                    d.animateToNextChild.apply(c, [b])
                }, e.autoplayDuration);
                e.autoplayIsRunning = true;
                c.trigger("autoplayStart")
            })
        },
        stopAutoplay: function(b) {
            return this.each(function() {
                clearInterval(a(this).data("roundabout").autoplayInterval);
                a(this).data("roundabout").autoplayInterval = null;
                a(this).data("roundabout").autoplayIsRunning = false;
                if (!b) {
                    a(this).unbind(".autoplay")
                }
                a(this).trigger("autoplayStop")
            })
        },
        toggleAutoplay: function(b) {
            return this.each(function() {
                var c = a(this),
                    e = c.data("roundabout");
                b = b || e.autoplayCallback || function() {};
                if (!d.isAutoplaying.apply(a(this))) {
                    d.startAutoplay.apply(a(this), [b])
                } else {
                    d.stopAutoplay.apply(a(this), [b])
                }
            })
        },
        isAutoplaying: function() {
            return this.data("roundabout").autoplayIsRunning
        },
        changeAutoplayDuration: function(b) {
            return this.each(function() {
                var c = a(this),
                    e = c.data("roundabout");
                e.autoplayDuration = b;
                if (d.isAutoplaying.apply(c)) {
                    d.stopAutoplay.apply(c);
                    setTimeout(function() {
                        d.startAutoplay.apply(c)
                    }, 10)
                }
            })
        },
        normalize: function(a) {
            var b = a % 360;
            return b < 0 ? 360 + b : b
        },
        normalizeRad: function(a) {
            while (a < 0) {
                a += Math.PI * 2
            }
            while (a > Math.PI * 2) {
                a -= Math.PI * 2
            }
            return a
        },
        isChildBackDegreesBetween: function(b, c) {
            var d = a(this).data("roundabout").backDegrees;
            if (b > c) {
                return d >= c && d < b
            } else {
                return d < c && d >= b
            }
        },
        getAnimateToMethod: function(a) {
            a = a.toLowerCase();
            if (a === "next") {
                return "animateToNextChild"
            } else if (a === "previous") {
                return "animateToPreviousChild"
            }
            return "animateToNearestChild"
        },
        relayoutChildren: function() {
            return this.each(function() {
                var b = a(this),
                    c = a.extend({}, b.data("roundabout"));
                c.startingChild = b.data("roundabout").childInFocus;
                d.init.apply(b, [c, null, true])
            })
        },
        getNearestChild: function() {
            var b = a(this),
                c = b.data("roundabout"),
                d = b.children(c.childSelector).length;
            if (!c.reflect) {
                return (d - Math.round(c.bearing / c.period) % d) % d
            } else {
                return Math.round(c.bearing / c.period) % d
            }
        },
        degToRad: function(a) {
            return d.normalize.apply(null, [a]) * Math.PI / 180
        },
        getPlacement: function(a) {
            var b = this.data("roundabout");
            return !b.reflect ? 360 - b.period * a : b.period * a
        },
        isInFocus: function(a) {
            var b, c = this,
                e = c.data("roundabout"),
                f = d.normalize.apply(null, [e.bearing]);
            a = d.normalize.apply(null, [a]);
            b = Math.abs(f - a);
            return b <= e.floatComparisonThreshold || b >= 360 - e.floatComparisonThreshold
        },
        getChildInFocus: function() {
            var b = a(this).data("roundabout");
            return b.childInFocus > -1 ? b.childInFocus : false
        },
        compareVersions: function(a, b) {
            var c, d = a.split(/\./i),
                e = b.split(/\./i),
                f = d.length > e.length ? d.length : e.length;
            for (c = 0; c <= f; c++) {
                if (d[c] && !e[c] && parseInt(d[c], 10) !== 0) {
                    return 1
                } else if (e[c] && !d[c] && parseInt(e[c], 10) !== 0) {
                    return -1
                } else if (d[c] === e[c]) {
                    continue
                }
                if (d[c] && e[c]) {
                    if (parseInt(d[c], 10) > parseInt(e[c], 10)) {
                        return 1
                    } else {
                        return -1
                    }
                }
            }
            return 0
        }
    };
    a.fn.roundabout = function(b) {
        if (d[b]) {
            return d[b].apply(this, Array.prototype.slice.call(arguments, 1))
        } else if (typeof b === "object" || a.isFunction(b) || !b) {
            return d.init.apply(this, arguments)
        } else {
            a.error("Method " + b + " does not exist for jQuery.roundabout.")
        }
    }
})(jQuery)