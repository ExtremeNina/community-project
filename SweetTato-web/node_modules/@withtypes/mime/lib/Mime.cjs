/**
 * name: @withtypes/mime
 * version: v0.1.2
 * description: MIME with types.
 * author: chengpeiquan <chengpeiquan@chengpeiquan.com>
 * homepage: https://github.com/chengpeiquan/withtypes/tree/main/packages/mime
 * license: MIT
 */
"use strict";function r(){this._types=Object.create(null),this._extensions=Object.create(null);for(let t=0;t<arguments.length;t++)this.define(arguments[t]);this.define=this.define.bind(this),this.getType=this.getType.bind(this),this.getExtension=this.getExtension.bind(this)}r.prototype.define=function(t,n){for(let e in t){let o=t[e].map(function(s){return s.toLowerCase()});e=e.toLowerCase();for(let s=0;s<o.length;s++){const i=o[s];if(i[0]!=="*"){if(!n&&i in this._types)throw new Error('Attempt to change mapping for "'+i+'" extension from "'+this._types[i]+'" to "'+e+'". Pass `force=true` to allow this, otherwise remove "'+i+'" from the list of extensions for "'+e+'".');this._types[i]=e}}if(n||!this._extensions[e]){const s=o[0];this._extensions[e]=s[0]!=="*"?s:s.substr(1)}}};r.prototype.getType=function(t){t=String(t);let n=t.replace(/^.*[/\\]/,"").toLowerCase(),e=n.replace(/^.*\./,"").toLowerCase(),o=n.length<t.length;return(e.length<n.length-1||!o)&&this._types[e]||null};r.prototype.getExtension=function(t){return t=/^\s*([^;\s]*)/.test(t)&&RegExp.$1,t&&this._extensions[t.toLowerCase()]||null};var h=r;module.exports=h;
