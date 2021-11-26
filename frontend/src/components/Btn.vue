<template>
  <button @click="$emit('click')" class="button"><slot/></button>
</template>

<script>
  export default {
    name: "Btn"
  }
</script>

<style scoped lang="scss">
  button {
    cursor: pointer;
    background: var(--background);
    border-radius: 32px;
    color: var(--text-color);
    border: solid black;
    padding: 5px 15px;
    font-size: 24px;
    line-height: 1em;

    &[purple] {
      border-color: var(--stroke);
    }

    &[black] {
      border-color: var(--black);
    }

    &[grey] {
      border-color: var(--grey);
    }

    &[just-a-little-red] {
      border-color: var(--just-a-little-red);
    }

    &[thin] {
      border-width: 1px;
    }

    &[thick] {
      border-width: 2px;
    }

    &[big] {
      height: 66px;
      border-radius: 15px;
    }

    &[disabled] {
      opacity: 0.5;
      pointer-events: none;
    }

    &[medium] {
      height: 50px;
      font-size: 18px;
      padding-left: 20px;
      padding-right: 20px;
      border-radius: 10px;
    }

    &[small] {
      height: 43px;
      font-size: 18px;
      border-radius: 5px;
    }
  }
</style>
