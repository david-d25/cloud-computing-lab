<template>
  <div class="style_selector">
    <div class="title">Выберите стили</div>
    <div class="styles" v-if="styles.length">
      <label class="style_wr"
             v-for="style in styles"
             :key="style.id">
        <input type="checkbox"
               class="style_checkbox"
               :value="style.id"
               v-model="localValue"
               @click="onStyleClick(style)"/>
        <span class="style">
          {{style.title}}
          <span v-if="style.sensitive">&#9889;</span>
        </span>
      </label>
    </div>
    <div class="no_styles" v-else>
      В системе ещё не настроили стили :(
    </div>
    <dialog-popup red-background :show="sensitiveStyleWarningTarget" @close="onSensitiveStyleWarningClose">
      <div class="dialog_title">&#9888;&#65039; &#9888;&#65039; &#9888;&#65039;</div>
      <div class="dialog_title bold">Sensitive content ahead!</div>
      <div class="dialog_text">Этот стиль может содержать оскорбления, нецензурную брань и ещё много плохих вещей.</div>
      <btn very-big thick on-dark-background class="dialog_cancel_btn" @click="onSensitiveStyleWarningClose">
        Выберу что-то другое
      </btn>
      <div class="dialog_proceed_btn" @click="sensitiveStyleWarningTarget = null">Я понимаю, продолжить</div>
    </dialog-popup>
  </div>
</template>

<script>
import DialogPopup from "#/components/dialogs/DialogPopup";
import Btn from "#/components/Btn";
export default {
  name: "StyleSelector",
  components: {Btn, DialogPopup},
  props: {
    styles: Array,
    value: Array
  },
  data() {
    return {
      localValue: [],
      sensitiveStyleWarningTarget: null
    }
  },
  mounted() {
    this.localValue = this.value;
  },
  watch: {
    localValue() {
      this.$emit('input', this.localValue);
    },
    value() {
      this.localValue = this.value;
    }
  },
  methods: {
    onSensitiveStyleWarningClose() {
      this.localValue.splice(this.localValue.indexOf(this.sensitiveStyleWarningTarget), 1);
      this.sensitiveStyleWarningTarget = null;
    },
    onStyleClick(style) {
      if (!this.localValue.includes(style.id) && style.sensitive)
        this.sensitiveStyleWarningTarget = style.id;
    }
  }
}
</script>

<style scoped lang="scss">
  .title {
    font-size: 24px;
    margin-bottom: 17px;
  }

  .styles {
    margin: 25px 0;
  }

  .no_styles {
    font-size: 0.8em;
    color: var(--dark-grey);
    margin: 25px 0;
    padding: 10px;
  }

  .style_wr {
    .style {
      display: inline-block;
      padding: 16px 32px;
      background: var(--light-grey);
      font-size: 18px;
      margin: 5px;
      border-radius: 100px;
    }

    .style_checkbox {
      opacity: 0;
      width: 0;
      height: 0;
      margin: 0;

      &:checked + .style {
        background: var(--fill);
      }
    }
  }

  .dialog_title {
    color: white;
    font-size: 1.2em;
    margin-bottom: 10px;

    &.bold {
      font-weight: bold;
    }
  }

  .dialog_text {
    color: white;
    margin-bottom: 25px;
  }

  .dialog_cancel_btn {
    margin-bottom: 30px;
  }

  .dialog_proceed_btn {
    color: white;
    text-decoration: underline;
    cursor: pointer;
    font-size: 0.8em;
    margin-bottom: 30px;
  }
</style>
