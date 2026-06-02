/**
 * name: @withtypes/mime
 * version: v0.1.2
 * description: MIME with types.
 * author: chengpeiquan <chengpeiquan@chengpeiquan.com>
 * homepage: https://github.com/chengpeiquan/withtypes/tree/main/packages/mime
 * license: MIT
 */
(function(s,r){typeof exports=="object"&&typeof module<"u"?module.exports=r():typeof define=="function"&&define.amd?define(r):(s=typeof globalThis<"u"?globalThis:s||self,s.Mime=r())})(this,function(){"use strict";function s(){this._types=Object.create(null),this._extensions=Object.create(null);for(let e=0;e<arguments.length;e++)this.define(arguments[e]);this.define=this.define.bind(this),this.getType=this.getType.bind(this),this.getExtension=this.getExtension.bind(this)}s.prototype.define=function(e,i){for(let t in e){let h=e[t].map(function(n){return n.toLowerCase()});t=t.toLowerCase();for(let n=0;n<h.length;n++){const o=h[n];if(o[0]!=="*"){if(!i&&o in this._types)throw new Error('Attempt to change mapping for "'+o+'" extension from "'+this._types[o]+'" to "'+t+'". Pass `force=true` to allow this, otherwise remove "'+o+'" from the list of extensions for "'+t+'".');this._types[o]=t}}if(i||!this._extensions[t]){const n=h[0];this._extensions[t]=n[0]!=="*"?n:n.substr(1)}}},s.prototype.getType=function(e){e=String(e);let i=e.replace(/^.*[/\\]/,"").toLowerCase(),t=i.replace(/^.*\./,"").toLowerCase(),h=i.length<e.length;return(t.length<i.length-1||!h)&&this._types[t]||null},s.prototype.getExtension=function(e){return e=/^\s*([^;\s]*)/.test(e)&&RegExp.$1,e&&this._extensions[e.toLowerCase()]||null};var r=s;return r});
