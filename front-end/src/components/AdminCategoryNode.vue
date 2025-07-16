<template>
  <div>
    <div
      class="flex items-center justify-between py-1 group relative"
    >
      <div class="flex items-center gap-2">
        <span
          v-if="!isEditing"
          @click="enableEditing"
          class="cursor-pointer text-gray-800 hover:underline"
        >
          {{ editableName || '(Без име)' }}
        </span>
        <input
          v-else
          v-model="editableName"
          class="border-b border-pink-400 focus:outline-none focus:border-pink-600 text-sm px-1"
          @blur="onBlur"
          @keyup.enter="onEnter"
          ref="inputRef"
        />
        <button
          class="text-pink-600 hover:text-pink-800 text-xl"
          @click.stop="addChild"
          title="Добави подкатегория"
        >
          +
        </button>
      </div>

      <div class="flex gap-2 items-center absolute right-2">
        <button
          class="opacity-0 group-hover:opacity-100 transition-opacity text-gray-500 hover:text-gray-700 text-sm"
          @click="goToEdit"
          title="Редактирай категория"
        >
          ✏️
        </button>
        <button
          class="opacity-0 group-hover:opacity-100 transition-opacity text-red-500 hover:text-red-700 text-sm"
          @click="confirmDelete"
          title="Изтрий категория"
        >
          🗑️
        </button>
      </div>
    </div>

    <div v-if="category.children && category.children.length" class="ml-4 border-l border-gray-200 pl-4">
      <AdminCategoryNode
        v-for="child in category.children"
        :key="child.id || child.tempId"
        :category="child"
        @add-child="$emit('add-child', $event)"
        @refresh="$emit('refresh')"
        @update-name="$emit('update-name', $event)"
        @remove-temp="$emit('remove-temp', $event)"
        @delete-category="$emit('delete-category', $event)"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, watch, onMounted, nextTick, defineProps, defineEmits } from 'vue';
import AdminCategoryNode from '@/components/AdminCategoryNode.vue';
import { useRouter } from 'vue-router';

const router = useRouter();

const props = defineProps({
  category: Object
});
const emit = defineEmits(['add-child', 'refresh', 'update-name', 'remove-temp', 'delete-category']);

const editableName = ref(props.category.name);
const isEditing = ref(props.category.isNew || false);
const hasSaved = ref(false);
const inputRef = ref(null);

onMounted(() => {
  if (isEditing.value) {
    nextTick(() => inputRef.value && inputRef.value.focus());
  }
});

watch(() => props.category.name, (newName) => {
  editableName.value = newName;
});

function enableEditing() {
  isEditing.value = true;
  hasSaved.value = false;
  nextTick(() => inputRef.value && inputRef.value.focus());
}

function emitName() {
  if (!hasSaved.value && editableName.value.trim() !== '') {
    hasSaved.value = true;
    emit('update-name', {
      id: props.category.id ?? props.category.tempId,
      name: editableName.value.trim()
    });
  }
}

function onEnter() {
  emitName();
  isEditing.value = false;
}

function onBlur() {
  emitName();
  isEditing.value = false;
}

function addChild() {
  emit('add-child', props.category.id ?? props.category.tempId);
}

function confirmDelete() {
  if (confirm('Сигурни ли сте, че искате да изтриете тази категория?')) {
    if (props.category.id) {
      emit('delete-category', props.category.id);
    } else {
      emit('remove-temp', props.category.tempId);
    }
  }
}
function goToEdit() {
  if (props.category.id) {
    router.push(`/admin/categories/${props.category.id}/edit`);
  }
}
</script>

<style scoped>
input[type='text'], input[type='file'], textarea {
  font-size: 0.875rem;
}
</style>
