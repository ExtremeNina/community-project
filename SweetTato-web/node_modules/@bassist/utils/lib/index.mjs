/**
 * name: @bassist/utils
 * version: v0.4.0
 * description: Opinionated collection of common JavaScript / TypeScript utils by @chengpeiquan .
 * author: chengpeiquan <chengpeiquan@chengpeiquan.com>
 * homepage: https://paka.dev/npm/@bassist/utils
 * license: MIT
 */
var S = Object.defineProperty;
var b = (t, e, n) => e in t ? S(t, e, { enumerable: !0, configurable: !0, writable: !0, value: n }) : t[e] = n;
var u = (t, e, n) => (b(t, typeof e != "symbol" ? e + "" : e, n), n);
import p from "@withtypes/mime";
const A = {
  isSupported: typeof navigator > "u" ? !1 : Boolean(navigator.clipboard),
  async copy(t) {
    if (!this.isSupported)
      return !1;
    const e = t.innerText || t.value;
    return await this.write(e);
  },
  async cut(t) {
    return !this.isSupported || !await this.copy(t) ? !1 : (t.value = "", !0);
  },
  async read() {
    return this.isSupported ? await navigator.clipboard.readText() : "";
  },
  async write(t) {
    return this.isSupported ? (await navigator.clipboard.writeText(t), !0) : !1;
  }
};
function h(t) {
  return Object.prototype.toString.call(t).slice(8, -1).toLowerCase();
}
function g(t) {
  return h(t) === "object";
}
function d(t, e) {
  return g(t) ? Object.prototype.hasOwnProperty.call(t, e) : !1;
}
const c = typeof window < "u", C = !c, o = c ? navigator.userAgent.toLowerCase() : "", m = /iPhone|phone|android|iPod|pad|iPad/i, M = m.test(o), Q = !m.test(o), R = /Android/i.test(o), T = /iPhone|iPod|iPad|iOS/i.test(o), _ = /uni-app|html5plus/.test(o), I = /MicroMessenger/i.test(o), z = /\sQQ|mqqbrowser|qzone|qqbrowser/i.test(o), N = /mqqbrowser|qqbrowser/i.test(o), P = /qzone\/.*_qz_([\d.]+)/i.test(o), j = /(weibo).*weibo__([\d.]+)/i.test(o), B = /(baiduboxapp)\/([\d.]+)/i.test(o);
function v(t) {
  return t.startsWith("data") && t.includes("base64") ? t.split(",")[0].replace(/data:(.*);base64/, "$1") : p.getType(t) || "";
}
function E(t) {
  return p.getExtension(t) || "";
}
function D(t) {
  const e = v(t);
  return E(e);
}
function L(t, e = !1) {
  return t = t ? String(t) : "", t = t.replace(/[^\d]/g, ""), t && !e && (t = parseInt(t)), String(t);
}
function U(t) {
  if (t = String(t), !t)
    return "0.00";
  const e = t.split("."), n = e[0], r = e[1];
  return e.length === 1 ? `${n}.00` : r.length === 1 ? `${t}0` : Number(t).toFixed(2);
}
function W(t, e) {
  return String(t).length > e ? String(t).slice(0, e) + "…" : String(t);
}
function Z([t, ...e]) {
  return t ? (t == null ? void 0 : t.toUpperCase()) + e.join("").toLowerCase() : "";
}
function F({ type: t, id: e, resource: n }) {
  return new Promise((r, i) => {
    if (!c || document.querySelector(e)) {
      i();
      return;
    }
    function a(s) {
      s.addEventListener("load", r), s.addEventListener("error", i), s.addEventListener("abort", i);
    }
    switch (t) {
      case "js": {
        const s = document.createElement("script");
        s.async = !0, s.src = n, a(s), document.head.appendChild(s);
        break;
      }
      case "css": {
        const s = document.createElement("link");
        s.id = e, s.rel = "stylesheet", s.href = n, a(s), document.head.appendChild(s);
        break;
      }
      case "style": {
        const s = document.createElement("style");
        s.id = e, a(s), document.head.appendChild(s), s.appendChild(document.createTextNode(n));
        break;
      }
    }
  });
}
function y(t) {
  let e = "";
  if (c) {
    const { search: r } = window.location;
    e = r;
  }
  if (typeof t == "string" && t.startsWith("http")) {
    const r = t.indexOf("?");
    e = r === -1 ? "" : t.slice(r);
  }
  if (e.includes("#")) {
    const r = e.indexOf("#");
    e = e.slice(0, r);
  }
  if (!e.length)
    return {};
  const n = {};
  return e.slice(1).split("&").forEach((r) => {
    const [i, a] = r.split("=");
    n[i] = decodeURIComponent(a);
  }), n;
}
function J(t) {
  const e = y(t), n = {};
  return Object.keys(e).forEach((i) => {
    i !== "path" && (n[i] = e[i]);
  }), { path: e.path || "", params: n };
}
function V(t, e) {
  return y(e)[t] || "";
}
function K(t) {
  return g(t) ? Object.keys(t).map((e) => `${e}=${encodeURIComponent(String(t[e]))}`).join("&") : "";
}
function x(t = 0, e = 100, n = "round") {
  return Math[n](Math.random() * (e - t) + t);
}
function X(t = 10) {
  const e = "useandom-26T198340PX75pxJACKVERYMINDBUSHWOLF_GQZbfghjklqvwyzrict";
  let n = "", r = t;
  const i = e.length;
  for (; r--; )
    n += e[Math.random() * i | 0];
  return n;
}
function G() {
  const t = x(0, 1);
  return [!0, !1][t];
}
function H(t) {
  if (!Array.isArray(t))
    return t;
  for (let e = t.length - 1; e > 0; e--) {
    const n = Math.floor(Math.random() * (e + 1)), r = t[e];
    t[e] = t[n], t[n] = r;
  }
  return t;
}
function Y(t) {
  return /^1[3456789]\d{9}$/.test(String(t));
}
function tt(t) {
  return /^[A-Za-z\d]+([-_.][A-Za-z\d]+)*@([A-Za-z\d]+[-.])+[A-Za-z\d]{2,4}$/.test(
    t
  );
}
function et(t) {
  return /https?:\/\/[\w-]+(\.[\w-]+){1,2}(\/[\w-]{3,6}){0,2}(\?[\w_]{4,6}=[\w_]{4,6}(&[\w_]{4,6}=[\w_]{4,6}){0,2})?/.test(
    t
  );
}
function nt(t) {
  const e = /^([1-6][1-9]|50)\d{4}(18|19|20)\d{2}((0[1-9])|10|11|12)(([0-2][1-9])|10|20|30|31)\d{3}[0-9Xx]$/, n = /^([1-6][1-9]|50)\d{4}\d{2}((0[1-9])|10|11|12)(([0-2][1-9])|10|20|30|31)\d{3}$/;
  return e.test(t) || n.test(t);
}
function rt(t) {
  return /^([1-9]{1})(\d{15}|\d{16}|\d{18})$/.test(t);
}
function k() {
  return process.env.NODE_ENV;
}
const st = k();
function f(t) {
  return process.env.NODE_ENV === t;
}
const it = f("development"), ot = f("test"), at = f("production"), l = {};
class O {
  constructor(e) {
    u(this, "data");
    this.data = {};
    const n = d(l, e);
    this.data = n ? l[e].data : {}, n || (l[e] = this);
  }
  get length() {
    return Object.keys(this.data).length;
  }
  clear() {
    this.data = {};
  }
  getItem(e) {
    return d(this.data, e) ? this.data[e] : null;
  }
  setItem(e, n) {
    this.data[e] = n;
  }
  removeItem(e) {
    d(this.data, e) && delete this.data[e];
  }
  key(e) {
    const n = Object.keys(this.data);
    return e > n.length ? null : n[e];
  }
}
class w {
  constructor(e, n) {
    u(this, "prefix");
    u(this, "storage");
    this.prefix = e, this.storage = c ? window[n] : new O(e);
  }
  get(e) {
    const n = this.storage.getItem(`${this.prefix}-${e}`);
    if (!n)
      return n;
    try {
      return n === "true" ? !0 : n === "false" ? !1 : n === "null" ? null : n === "undefined" ? void 0 : JSON.parse(n);
    } catch {
      return n;
    }
  }
  set(e, n) {
    try {
      const i = h(n) === "string" ? n : JSON.stringify(n);
      this.storage.setItem(`${this.prefix}-${e}`, i);
    } catch (r) {
      console.log(r);
    }
  }
  remove(e) {
    this.storage.removeItem(`${this.prefix}-${e}`);
  }
  clear() {
    this.list().forEach((n) => {
      this.remove(n);
    });
  }
  count() {
    return this.list().length;
  }
  list() {
    if (!this.prefix)
      return [];
    const e = [], n = this.storage.length;
    for (let r = 0; r < n; r++) {
      const i = this.storage.key(r);
      i != null && i.startsWith(this.prefix) && e.push(i.replace(`${this.prefix}-`, ""));
    }
    return e;
  }
}
class ct extends w {
  constructor(e) {
    super(e, "localStorage");
  }
}
class ut extends w {
  constructor(e) {
    super(e, "sessionStorage");
  }
}
export {
  ct as LocalStorage,
  ut as SessionStorage,
  Z as capitalize,
  f as checkRuntimeEnv,
  A as clipboard,
  W as ellipsis,
  L as extractNumber,
  J as extractQueryInfo,
  U as formatAmount,
  h as getDataType,
  D as getExtension,
  E as getExtensionFromMimeType,
  v as getMimeType,
  V as getQuery,
  k as getRuntimeEnv,
  d as hasKey,
  R as isAndroid,
  B as isBaidu,
  rt as isBankCard,
  c as isBrowser,
  Q as isDesktop,
  it as isDevRuntime,
  tt as isEmail,
  T as isIOS,
  nt as isIdCard,
  Y as isMob,
  M as isMobile,
  g as isObject,
  at as isProdRuntime,
  z as isQQ,
  N as isQQBrowser,
  P as isQzone,
  C as isServer,
  ot as isTestRuntime,
  _ as isUniApp,
  et as isUrl,
  j as isWeibo,
  I as isWeixin,
  F as loadRes,
  m as mobileDevicesRegExp,
  y as parseQuery,
  G as randomBoolean,
  x as randomNumber,
  X as randomString,
  st as runtimeEnv,
  H as shuffle,
  K as stringifyQuery,
  o as ua
};
