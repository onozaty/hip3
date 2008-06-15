// ==UserScript==
// @name           talk page
// @namespace      http://www.enjoyxstudy.com/
// @include        http://*
// @exclude        http://localhost*
// @exclude        http://127.0.0.1*
// ==/UserScript==

if (window != window.parent) return;

var message = encodeURIComponent(document.title) + ' ' + encodeURIComponent(location.href);

GM_xmlhttpRequest({
  method: 'POST',
  url: 'http://localhost:3333/talk/',
  headers:{'Content-type':'application/x-www-form-urlencoded'},
  data: 'message=' + message
});
