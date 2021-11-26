<template>
  <container>
    <loading-content class="config_wr" :status="configStatus" @reload="reloadConfig">
      <div class="config" v-if="config.length > 0">
        <div  class="config_item"
              v-for="(item, index) in config"
              :key="item.id">
          <text-input class="input" hint="Имя" v-model="item.name"/>
          <text-input class="input" hint="Значение" v-model="item.value"/>
          <btn small thick just-a-little-red class="remove_btn" @click="config.splice(index, 1)">
            &#10060; Удалить
          </btn>
        </div>
      </div>
      <div style="color: var(--grey); transition: none" v-if="config.length === 0 && configStatus === 'ready'">
        Пусто...
      </div>
    </loading-content>
    <btn small grey thick class="add_btn" @click="newConfigItem">&#10133; Добавить</btn>
    <div class="config_controls">
      <btn small black thick class="btn save_btn" @click="saveConfig">&#x1f44c; Сохранить</btn>
      <btn small black thick class="btn reset_btn" @click="resetConfigDialog = true">&#128260; Сбросить</btn>
    </div>
    <dialog-popup :show="resetConfigDialog" @close="resetConfigDialog = false">
      <div class="title">Внесённые изменения будут утеряны</div>
      <btn black thick medium class="dialog_button" @click="resetConfig">Сбросить</btn>
      <btn black thick medium class="dialog_button" @click="resetConfigDialog = false">Ой, не надо</btn>
    </dialog-popup>
  </container>
</template>

<script>
import Container from "#/components/Container";
import LoadingContent from "#/components/LoadingContent";
import TextInput from "#/components/TextInput";
import Btn from "#/components/Btn";
import axios from "axios";
import Vue from "vue";
import TransitionExpand from "#/components/TransitionExpand";
import DialogPopup from "#/components/dialogs/DialogPopup";

export default {
  name: "Config",
  components: {DialogPopup, TransitionExpand, Btn, TextInput, LoadingContent, Container },
  data() {
    return {
      originalConfig: [],
      config: [],
      resetConfigDialog: false,
      configStatus: 'loading'
    }
  },
  mounted() {
    this.reloadConfig();
  },
  methods: {
    async reloadConfig() {
      try {
        this.configStatus = 'loading';
        let dto = (await axios.get('/api/manage/config')).data;
        Vue.set(this, 'config', []);
        for (const [name, value] of Object.entries(dto['config'])) {
          this.config.push({ name, value });
        }
        Vue.set(this, 'originalConfig', JSON.parse(JSON.stringify(this.config)));
        this.configStatus = 'ready';
      } catch (e) {
        this.configStatus = e.request.status === 0 ? 'offline' : 'error';
      }

    },
    async saveConfig() {
      try {
        this.configStatus = 'loading';
        let dto = {};
        this.config.filter(item => item.name.trim().length > 0).forEach(item => dto[item.name] = item.value);
        await axios.post('/api/manage/config', { config: dto });
        await this.reloadConfig();
      } catch (e) {
        this.configStatus = e.request.status === 0 ? 'offline' : 'error';
      }
    },
    newConfigItem() {
      this.config.push({ name: '', value: '' });
    },
    resetConfig() {
      Vue.set(this, 'config', JSON.parse(JSON.stringify(this.originalConfig)));
      this.resetConfigDialog = false;
    }
  }
}
</script>

<style scoped lang="scss">
  .config_wr {
    margin-bottom: 25px;
  }

  .add_btn {
    width: 100%;
    margin-bottom: 20px;
  }

  .remove_btn {
    width: 100%;
  }

  .input {
    margin-bottom: 12px;
  }

  .config_controls {
    display: flex;

    .btn {
      flex: 1;

      &:first-child {
        margin-right: 20px;
      }
    }
  }

  .title {
    font-size: 24px;
    margin-bottom: 24px;
  }

  .dialog_button {
    min-width: 320px;
    margin-bottom: 20px;
  }

  .config_item {
    border-radius: 5px;
    outline: 1px solid var(--grey);
    box-shadow: 0 2px 0 var(--grey);
    margin-bottom: 20px;
    padding: 16px;
  }
</style>
