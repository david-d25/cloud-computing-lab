<template>
  <dialog-popup class="settings_dialog" @close="$emit('close')" :show="show">

    <div class="title">Настройки</div>

    <toggle-switch class="dark_theme_switch" v-model="darkTheme">Темная тема</toggle-switch>

    <router-link to="/admin" v-if="$route.path !== '/admin'">
      <btn medium thick black class="button" @click="$store.commit('closeSettings')">
        &#128273; Админ-панель
      </btn>
    </router-link>

    <btn medium thick grey class="button" @click="$emit('close')">Закрыть</btn>

  </dialog-popup>
</template>

<script>
  import DialogPopup from "#/components/dialogs/DialogPopup";
  import ToggleSwitch from "#/components/ToggleSwitch";
  import Btn from "#/components/Btn";

  export default {
    name: "SettingsDialog",
    components: {Btn, ToggleSwitch, DialogPopup},
    props: ['show'],
    data() {
      return {
        darkTheme: this.$store.state.theme === 'dark'
      }
    },
    watch: {
      darkTheme() {
        this.$store.commit('setTheme', this.darkTheme ? 'dark' : 'light');
      }
    }
  }
</script>

<style scoped>
  .settings_dialog {
    text-align: center;
  }

  .title {
    font-size: 32px;
    font-weight: bold;
    margin-bottom: 25px;
  }

  .dark_theme_switch {
    margin-bottom: 25px;
  }

  .button {
    display: block;
    margin: auto auto 25px;
  }
</style>
