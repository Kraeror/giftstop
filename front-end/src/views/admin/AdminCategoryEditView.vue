<template>
  <div class="max-w-4xl mx-auto py-10 px-4">
    <h1 class="text-3xl font-bold text-center text-gray-800 mb-6">Редакция на категория</h1>

    <form @submit.prevent="saveCategory" class="bg-white shadow-md rounded px-8 pt-6 pb-8 mb-4 space-y-6">
      <div>
        <label class="block text-gray-700 text-sm font-bold mb-2" for="name">Име</label>
        <input v-model="form.name" id="name" type="text" placeholder="Име на категория"
          class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:ring-2 focus:ring-pink" />
      </div>

      <div>
        <label class="block text-gray-700 text-sm font-bold mb-2" for="slug">Slug</label>
        <input v-model="form.slug" id="slug" type="text" placeholder="slug"
          class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:ring-2 focus:ring-pink" />
      </div>

      <div>
        <label class="block text-gray-700 text-sm font-bold mb-2" for="metaTitle">Meta заглавие</label>
        <input v-model="form.metaTitle" id="metaTitle" type="text" placeholder="Meta заглавие"
          class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:ring-2 focus:ring-pink" />
      </div>

      <div>
        <label class="block text-gray-700 text-sm font-bold mb-2" for="metaDescription">Meta описание</label>
        <textarea v-model="form.metaDescription" id="metaDescription" rows="3"
          class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:ring-2 focus:ring-pink"></textarea>
      </div>

      <div class="mb-2">
        <label class="block text-gray-700 text-sm font-bold mb-2">Снимка</label>
        <input ref="imageInput" accept="image/*" type="file" @change="handleFileUpload" class="hidden" />

        <button @click.prevent="$refs.imageInput.click()" type="button"
          class="inline-flex items-center gap-2 px-4 py-2 bg-[#F777AC] text-white text-sm font-semibold rounded hover:bg-pink-600 focus:outline-none focus:ring">
          📷 Качи снимка
        </button>

        <div v-if="form.imageUrl" class="mt-2 flex justify-center">
          <div class="relative w-[100px] h-[100px]">
            <img :src="form.imageUrl" class="w-full h-full object-cover rounded border" />
            <button @click.prevent="removeImage" class="absolute top-0 right-0 bg-red-500 text-white rounded-full p-1 text-xs">✕</button>
          </div>
        </div>

        <div v-if="form.image" class="text-sm text-gray-600 mt-1 text-center">
          {{ form.image.name }}
        </div>
      </div>

      <div class="pt-4">
        <button type="submit" class="bg-[#F777AC] hover:bg-pink-600 text-white font-bold py-2 px-4 rounded w-full">
          Запази промените
        </button>
      </div>
    </form>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import axios from 'axios';

const route = useRoute();
const router = useRouter();

const form = ref({
  name: '',
  slug: '',
  metaTitle: '',
  metaDescription: '',
  image: null,
  imageUrl: ''
});

async function fetchCategory() {
  try {
    const { id } = route.params;
    const res = await axios.get(`/api/admin/categories/${id}`);
    const cat = res.data;
    form.value.name = cat.name;
    form.value.slug = cat.slug;
    form.value.metaTitle = cat.metaTitle;
    form.value.metaDescription = cat.metaDescription;
    form.value.imageUrl = cat.imageUrl ? 'http://192.168.0.68:8080' + cat.imageUrl : '';
  } catch (e) {
    console.error('Грешка при зареждане на категорията:', e);
  }
}

function handleFileUpload(e) {
  const file = e.target.files[0];
  if (file) {
    form.value.image = file;
    form.value.imageUrl = URL.createObjectURL(file);
  }
}

function removeImage() {
  form.value.image = null;
  form.value.imageUrl = '';
}

async function saveCategory() {
  try {
    const { id } = route.params;
    const data = new FormData();
    data.append('name', form.value.name);
    data.append('slug', form.value.slug);
    data.append('metaTitle', form.value.metaTitle);
    data.append('metaDescription', form.value.metaDescription);
    if (form.value.image) {
      data.append('image', form.value.image);
    }

    await axios.patch(`/api/admin/categories/${id}`, data);
    alert('Категорията е запазена успешно.');
    router.push('/admin/categories');
  } catch (e) {
    console.error('Грешка при запис:', e);
  }
}

onMounted(fetchCategory);
</script>

<style scoped>
input,
textarea {
  background-color: white;
}
</style>
