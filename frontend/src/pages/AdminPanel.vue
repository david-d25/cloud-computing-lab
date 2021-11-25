<template>
  <div class="admin">
    <h1 class="title">Админ-панель</h1>

    <tabbed-view :tabs="[
      {
        id: 'agents',
        name: 'Агенты',
        default: true
      }, {
        id: 'config',
        name: 'Параметры'
      }
    ]">
      <template v-slot:agents><agents/></template>
      <template v-slot:config><config/></template>
    </tabbed-view>

    <div class="bottom_buttons">
      <btn small grey thin @click="$store.commit('openSettings')">&#9881;&#65039; Настройки</btn>
      <router-link to="/">
        <btn small grey thin>&#127968; Главная</btn>
      </router-link>
    </div>
  </div>
</template>

<script>
  import Btn from "#/components/Btn";
  import TabbedView from "#/components/TabbedView";
  import Agents from "#/components/Agents";
  import Config from "#/components/Config";

  export default {
    name: "AdminPanel",
    components: {Config, Agents, TabbedView, Btn}
  }
</script>

<style scoped>
  .admin {
    padding-top: 46px;
    text-align: center;
    min-height: 100vh;
  }

  .title {
    margin-bottom: 24px;
  }

  .bottom_buttons {
    display: flex;
    justify-content: space-between;
    width: 100%;
    padding: 35px;
  }

  h1 {
    text-transform: uppercase;
    margin: 0;
  }
</style>
