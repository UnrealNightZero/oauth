
import SignIn from './sign-in/SignIn'
import SignUp from './sign-up/SignUp'
import Homepage from './home/Home'
import { BrowserRouter, Routes, Route } from 'react-router-dom';
function App() {

  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Homepage />} />
        <Route path="/signUp" element={<SignUp />} />
        <Route path="/SignIn" element={<SignIn />} />
        </Routes>
    </BrowserRouter>
  )
}

export default App
