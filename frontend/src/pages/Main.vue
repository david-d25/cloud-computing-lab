<template>
  <div class="main">
    <container>
      <logo/>
      <div class="style_selection">
        <style-selector/>
        <btn big purple thick class="ready-btn">Готово</btn>
      </div>
      <div class="text_generation">

      </div>
    </container>

    <div class="bottom_buttons">
      <btn small grey thin @click="$store.commit('openSettings')">&#9881;&#65039; Настройки</btn>
    </div>
  </div>
</template>

<script>
import Container from "#/components/Container";
import Logo from "#/components/Logo";
import StyleSelector from "#/components/StyleSelector";
import Btn from "#/components/Btn";

export default {
  name: "Main",
  components: {Btn, StyleSelector, Logo, Container}
}
</script>

<style scoped>
  .main {
    padding-top: 46px;
    text-align: center;
    min-height: 100vh;
  }

  .ready-btn {
    width: 300px;
  }

  .bottom_buttons {
    display: flex;
    justify-content: center;
    width: 100%;
    padding: 35px;
  }
</style>
