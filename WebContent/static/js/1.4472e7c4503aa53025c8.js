webpackJsonp([1],{NGlO:function(t,e,n){"use strict";Object.defineProperty(e,"__esModule",{value:!0});var r={data:function(){return{src:"",loading:null}},methods:{resetSrc:function(t){this.src=t,this.load()},load:function(){this.loading=this.$loading({lock:!0,text:"loading...",spinner:"el-icon-loading",background:"rgba(0, 0, 0, 0.5)",target:document.querySelector("#main-container ")})},onloaded:function(){this.loading&&this.loading.close()}},mounted:function(){this.resetSrc(this.$store.state.iframe.iframeUrl)},watch:{$route:{handler:function(t,e){this.resetSrc(this.$store.state.iframe.iframeUrl)}}}},i={render:function(){var t=this.$createElement,e=this._self._c||t;return e("div",{staticClass:"iframe-container"},[e("iframe",{staticClass:"frame",attrs:{src:this.src,scrolling:"auto",frameborder:"0",onload:this.onloaded()}})])},staticRenderFns:[]};var a=n("VU/8")(r,i,!1,function(t){n("q0cV")},null,null);e.default=a.exports},q0cV:function(t,e){}});
//# sourceMappingURL=1.4472e7c4503aa53025c8.js.map