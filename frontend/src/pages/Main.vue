<template>
  <div class="main">
    <container>
      <logo/>

      <div class="style_selection" v-if="!stylesAreSelected">
        <loading-content hide-on-loading :status="allStylesStatus" @reload="reloadStyles">
          <style-selector :styles="allStyles" v-model="selectedStyles"/>
        </loading-content>
        <btn big purple thick class="ready_btn"
             @click="stylesAreSelected = true"
             :disabled="!allStyles.length">
          Готово
        </btn>
      </div>

      <div class="text_generation" v-else>
        <loading-content :status="generateStatus" @reload="generateStatus = 'ready'">
          <textarea class="textarea" placeholder="Введите текст сюда" v-model="text" @keydown.ctrl.enter="generate"></textarea>
        </loading-content>
        <div class="styles_hint">
          <span v-if="selectedStyles.length">
            Стили:
            <span class="styles_hint_item"
                  v-for="style in allStyles.filter(s => selectedStyles.includes(s.id))"
                  :key="style.id">{{style.title}}</span>
          </span>
          <span v-else>Без стиля</span>
        </div>
        <btn big purple thick class="generate_btn" @click="generate">Генерировать!</btn>
        <btn small thin black @click="stylesAreSelected = false">&#9999; Изменить стили</btn>
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
import LoadingContent from "#/components/LoadingContent";
import axios from "axios";

export default {
  name: "Main",
  components: {LoadingContent, Btn, StyleSelector, Logo, Container},
  data() {
    return {
      text: '',
      generateStatus: 'ready',
      stylesAreSelected: false,
      allStylesStatus: 'loading',
      allStyles: [],
      selectedStyles: []
    }
  },
  mounted() {
    this.reloadStyles();
  },
  methods: {
    async reloadStyles() {
      try {
        this.allStylesStatus = 'loading';
        this.allStyles = (await axios.get('/api/style')).data;
        this.allStylesStatus = 'ready';
      } catch (e) {
        this.allStylesStatus = e.request.status === 0 ? 'offline' : 'error';
      }
    },
    async generate() {
      try {
        this.generateStatus = 'loading';
        let styles = this.selectedStyles.join(',');
        let dto = (await axios.get(`/api/generate?text=${this.text.replaceAll('#', '%23')}&styles=${styles}`)).data;
        this.text = dto['beginning'] + dto['generated'];
        this.generateStatus = 'ready';
      } catch (e) {
        this.generateStatus = e.request.status === 0 ? 'offline' : 'error';
      }
    }
  }
}
</script>

<style scoped>
  .main {
    padding-top: 46px;
    text-align: center;
    min-height: 100vh;
  }

  .ready_btn {
    width: 300px;
  }

  .bottom_buttons {
    display: flex;
    justify-content: center;
    width: 100%;
    padding: 35px;
  }

  .textarea {
    max-width: 100%;
    max-height: 50vh;
    min-width: 100%;
    height: 25vh;
    resize: none;
    border-radius: 5px;
    padding: 12px;
    font-size: 1em;
    margin-bottom: 5px;
    border: 2px solid var(--black);
    background: var(--background);
  }

  .styles_hint {
    margin-bottom: 10px;
  }

  .generate_btn {
    width: 300px;
    display: block;
    margin: auto auto 21px;
  }

  .styles_hint * {
    color: var(--dark-grey);
  }

  .styles_hint_item:not(:last-child):after {
    display: inline-block;
    margin-right: 5px;
    content: ', ';
  }
</style>
