function e(t, e) {
    return t << e | t >>> 32 - e
}
function s(t, e) {
    var s, i, o, n, a;
    return o = 2147483648 & t,
    n = 2147483648 & e,
    s = 1073741824 & t,
    i = 1073741824 & e,
    a = (1073741823 & t) + (1073741823 & e),
    s & i ? 2147483648 ^ a ^ o ^ n: s | i ? 1073741824 & a ? 3221225472 ^ a ^ o ^ n: 1073741824 ^ a ^ o ^ n: a ^ o ^ n
}
function i(t, e, s) {
    return t & e | ~t & s
}
function o(t, e, s) {
    return t & s | e & ~s
}
function n(t, e, s) {
    return t ^ e ^ s
}
function a(t, e, s) {
    return e ^ (t | ~s)
}
function r(t, o, n, a, r, c, d) {
    return t = s(t, s(s(i(o, n, a), r), d)),
    s(e(t, c), o)
}
function c(t, i, n, a, r, c, d) {
    return t = s(t, s(s(o(i, n, a), r), d)),
    s(e(t, c), i)
}
function d(t, i, o, a, r, c, d) {
    return t = s(t, s(s(n(i, o, a), r), d)),
    s(e(t, c), i)
}
function l(t, i, o, n, r, c, d) {
    return t = s(t, s(s(a(i, o, n), r), d)),
    s(e(t, c), i)
}
function u(t) {
    var e, s = t.length,
    i = s + 8,
    o = (i - i % 64) / 64,
    n = 16 * (o + 1),
    a = Array(n - 1),
    r = 0,
    c = 0;
    while (c < s) e = (c - c % 4) / 4,
    r = c % 4 * 8,
    a[e] = a[e] | t.charCodeAt(c) << r,
    c++;
    return e = (c - c % 4) / 4,
    r = c % 4 * 8,
    a[e] = a[e] | 128 << r,
    a[n - 2] = s << 3,
    a[n - 1] = s >>> 29,
    a
}
function h(t) {
    var e, s, i = "",
    o = "";
    for (s = 0; s <= 3; s++) e = t >>> 8 * s & 255,
    o = "0" + e.toString(16),
    i += o.substr(o.length - 2, 2);
    return i
}
function m(t) {
    t = t.replace(/\r\n/g, "\n");
    for (var e = "",
    s = 0; s < t.length; s++) {
        var i = t.charCodeAt(s);
        i < 128 ? e += String.fromCharCode(i) : i > 127 && i < 2048 ? (e += String.fromCharCode(i >> 6 | 192), e += String.fromCharCode(63 & i | 128)) : (e += String.fromCharCode(i >> 12 | 224), e += String.fromCharCode(i >> 6 & 63 | 128), e += String.fromCharCode(63 & i | 128))
    }
    return e
}
function getSign(t) {
    var A, p, g, f, I, v, C, y, w, k = Array(),
    b = 7,
    S = 12,
    V = 17,
    E = 22,
    B = 5,
    O = 9,
    j = 14,
    x = 20,
    T = 4,
    Q = 11,
    N = 16,
    U = 23,
    D = 6,
    F = 10,
    R = 15,
    L = 21;
    for (t = m(t), k = u(t), v = 1732584193, C = 4023233417, y = 2562383102, w = 271733878, A = 0; A < k.length; A += 16) p = v,
    g = C,
    f = y,
    I = w,
    v = r(v, C, y, w, k[A + 0], b, 3614090360),
    w = r(w, v, C, y, k[A + 1], S, 3905402710),
    y = r(y, w, v, C, k[A + 2], V, 606105819),
    C = r(C, y, w, v, k[A + 3], E, 3250441966),
    v = r(v, C, y, w, k[A + 4], b, 4118548399),
    w = r(w, v, C, y, k[A + 5], S, 1200080426),
    y = r(y, w, v, C, k[A + 6], V, 2821735955),
    C = r(C, y, w, v, k[A + 7], E, 4249261313),
    v = r(v, C, y, w, k[A + 8], b, 1770035416),
    w = r(w, v, C, y, k[A + 9], S, 2336552879),
    y = r(y, w, v, C, k[A + 10], V, 4294925233),
    C = r(C, y, w, v, k[A + 11], E, 2304563134),
    v = r(v, C, y, w, k[A + 12], b, 1804603682),
    w = r(w, v, C, y, k[A + 13], S, 4254626195),
    y = r(y, w, v, C, k[A + 14], V, 2792965006),
    C = r(C, y, w, v, k[A + 15], E, 1236535329),
    v = c(v, C, y, w, k[A + 1], B, 4129170786),
    w = c(w, v, C, y, k[A + 6], O, 3225465664),
    y = c(y, w, v, C, k[A + 11], j, 643717713),
    C = c(C, y, w, v, k[A + 0], x, 3921069994),
    v = c(v, C, y, w, k[A + 5], B, 3593408605),
    w = c(w, v, C, y, k[A + 10], O, 38016083),
    y = c(y, w, v, C, k[A + 15], j, 3634488961),
    C = c(C, y, w, v, k[A + 4], x, 3889429448),
    v = c(v, C, y, w, k[A + 9], B, 568446438),
    w = c(w, v, C, y, k[A + 14], O, 3275163606),
    y = c(y, w, v, C, k[A + 3], j, 4107603335),
    C = c(C, y, w, v, k[A + 8], x, 1163531501),
    v = c(v, C, y, w, k[A + 13], B, 2850285829),
    w = c(w, v, C, y, k[A + 2], O, 4243563512),
    y = c(y, w, v, C, k[A + 7], j, 1735328473),
    C = c(C, y, w, v, k[A + 12], x, 2368359562),
    v = d(v, C, y, w, k[A + 5], T, 4294588738),
    w = d(w, v, C, y, k[A + 8], Q, 2272392833),
    y = d(y, w, v, C, k[A + 11], N, 1839030562),
    C = d(C, y, w, v, k[A + 14], U, 4259657740),
    v = d(v, C, y, w, k[A + 1], T, 2763975236),
    w = d(w, v, C, y, k[A + 4], Q, 1272893353),
    y = d(y, w, v, C, k[A + 7], N, 4139469664),
    C = d(C, y, w, v, k[A + 10], U, 3200236656),
    v = d(v, C, y, w, k[A + 13], T, 681279174),
    w = d(w, v, C, y, k[A + 0], Q, 3936430074),
    y = d(y, w, v, C, k[A + 3], N, 3572445317),
    C = d(C, y, w, v, k[A + 6], U, 76029189),
    v = d(v, C, y, w, k[A + 9], T, 3654602809),
    w = d(w, v, C, y, k[A + 12], Q, 3873151461),
    y = d(y, w, v, C, k[A + 15], N, 530742520),
    C = d(C, y, w, v, k[A + 2], U, 3299628645),
    v = l(v, C, y, w, k[A + 0], D, 4096336452),
    w = l(w, v, C, y, k[A + 7], F, 1126891415),
    y = l(y, w, v, C, k[A + 14], R, 2878612391),
    C = l(C, y, w, v, k[A + 5], L, 4237533241),
    v = l(v, C, y, w, k[A + 12], D, 1700485571),
    w = l(w, v, C, y, k[A + 3], F, 2399980690),
    y = l(y, w, v, C, k[A + 10], R, 4293915773),
    C = l(C, y, w, v, k[A + 1], L, 2240044497),
    v = l(v, C, y, w, k[A + 8], D, 1873313359),
    w = l(w, v, C, y, k[A + 15], F, 4264355552),
    y = l(y, w, v, C, k[A + 6], R, 2734768916),
    C = l(C, y, w, v, k[A + 13], L, 1309151649),
    v = l(v, C, y, w, k[A + 4], D, 4149444226),
    w = l(w, v, C, y, k[A + 11], F, 3174756917),
    y = l(y, w, v, C, k[A + 2], R, 718787259),
    C = l(C, y, w, v, k[A + 9], L, 3951481745),
    v = s(v, p),
    C = s(C, g),
    y = s(y, f),
    w = s(w, I);
    var M = h(v) + h(C) + h(y) + h(w);
    return M.toUpperCase()
}
