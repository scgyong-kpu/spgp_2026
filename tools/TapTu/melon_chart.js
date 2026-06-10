// run this in https://www.melon.com/chart/index.htm

const cleanText = (text) => (text || '').replace(/\u00a0/g, ' ').replace(/\s+/g, ' ').trim();

const songList = document.querySelector('.d_song_list');
const trs_50 = songList.querySelectorAll('tr.lst50')
const songs = []
for (const tr of trs_50) {
  const rankText = cleanText(tr.querySelector('span.rank')?.textContent);
  const rank = parseInt(rankText, 10);
  if (isNaN(rank) || rank > 20) continue;
  const thumbnail = tr.querySelector('div.wrap a.image_typeAll img')?.src || '';
  const title = cleanText(tr.querySelector('div.wrap_song_info div.rank01 a')?.textContent);
  const artist = cleanText(tr.querySelector('div.wrap_song_info div.rank02 a')?.textContent);
  const album = cleanText(tr.querySelector('div.wrap_song_info div.rank03 a')?.textContent);
  const song = {
    rank, title, artist, album, thumbnail,
  }
  songs.push(song)
}

console.log(songs)
console.log(JSON.stringify(songs, null, 2))
