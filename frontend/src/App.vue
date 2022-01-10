<template>
  <div id="root" :data-theme="$store.state.theme">
    <router-view :key="this.$route.fullPath"/>
    <settings-dialog :show="$store.state.settingsDialog" @close="$store.commit('closeSettings')"/>
  </div>
</template>

<script>
  import Vue from 'vue';
  import Vuex from 'vuex';
  import VueRouter from "vue-router";
  import Main from "#/pages/Main";
  import AdminPanel from "#/pages/AdminPanel";
  import DialogPopup from "#/components/dialogs/DialogPopup";
  import SettingsDialog from "#/components/dialogs/SettingsDialog";

  Vue.use(VueRouter);
  Vue.use(Vuex);

  const routes = [
    { path: '/admin', component: AdminPanel },
    { path: '*', component: Main }
  ];

  let router = new VueRouter({
    mode: 'history',
    routes
  });

  const store = new Vuex.Store({
    state: {
      settingsDialog: false,
      theme: 'light'
    },
    mutations: {
      openSettings(state) {
        state.settingsDialog = true;
      },
      closeSettings(state) {
        state.settingsDialog = false;
      },
      setTheme(state, theme) {
        state.theme = theme;
      }
    }
  });

  export default {
    name: "App",
    components: {
      SettingsDialog,
      DialogPopup,
      VueRouter
    },
    router,
    store
  }
</script>

<style lang="scss">
  @import "assets/style";
</style>
